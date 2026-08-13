#!/usr/bin/env python3
"""Génère CV ATS-friendly au format PDF depuis YAML."""

import argparse
import re
import sys
from pathlib import Path

import yaml
from jinja2 import Environment, FileSystemLoader, pass_eval_context
from markupsafe import Markup


def render_tasks(eval_ctx, raw_tasks: str) -> Markup:
    """
    Filtre Jinja2 personnalisé pour transformer le texte brut des tâches
    en HTML structuré (listes avec sous-listes).
    """
    lines = raw_tasks.strip().split('\n')
    html_parts = []
    in_sub_list = False

    for line in lines:
        stripped = line.strip()
        if not stripped:
            continue

        # Détecter niveau hiérarchique
        indent_level = len(line) - len(line.lstrip())

        # Ligne avec puce principale (▪)
        if stripped.startswith('▪'):
            if in_sub_list:
                html_parts.append('</ul>')
                in_sub_list = False
            text = stripped[1:].strip()
            # Vérifier si contient sous-liste sur même ligne
            html_parts.append(f'<li>{_escape_html(text)}')

        # Sous-élément (indenté ou avec -)
        elif stripped.startswith('-') or indent_level >= 4:
            if not in_sub_list:
                html_parts.append('<ul class="sub-list">')
                in_sub_list = True
            text = stripped.lstrip('-').strip()
            html_parts.append(f'<li>{_escape_html(text)}</li>')

        # Suite de ligne précédente (texte supplémentaire)
        else:
            if in_sub_list:
                html_parts.append('</ul>')
                in_sub_list = False
            # Fermer li ouvert si nécessaire
            if html_parts and html_parts[-1].startswith('<li>') and not html_parts[-1].endswith('</li>'):
                html_parts[-1] += f' {_escape_html(stripped)}</li>'
            else:
                html_parts.append(f'<p>{_escape_html(stripped)}</p>')

    if in_sub_list:
        html_parts.append('</ul>')
    # Fermer dernier li si ouvert
    if html_parts and html_parts[-1].startswith('<li>') and not html_parts[-1].endswith('</li>'):
        html_parts[-1] += '</li>'

    result = '<ul class="tasks">' + ''.join(html_parts) + '</ul>'
    return Markup(result) if eval_ctx.autoescape else result


def _escape_html(text: str) -> str:
    """Échappement minimal pour sécurité."""
    return (text
            .replace('&', '&')
            .replace('<', '<')
            .replace('>', '>'))


def format_month_year(value, format_str="%m/%Y"):
    """Filtre pour formater dates mois/année."""
    if isinstance(value, dict):
        month = value.get('month', 1)
        year = value.get('year', 0)
        from datetime import date
        try:
            d = date(year, month, 1)
            return d.strftime(format_str)
        except ValueError:
            return f"{month:02d}/{year}"
    return value


def load_yaml(filepath: Path) -> dict:
    """Charge et valide le YAML CV."""
    with open(filepath, 'r', encoding='utf-8') as f:
        data = yaml.safe_load(f)

    # Validations minimales
    required = ['firstName', 'lastName', 'email', 'experiences']
    missing = [f for f in required if f not in data]
    if missing:
        raise ValueError(f"Champs requis manquants: {missing}")

    return data


def generate_pdf(html_content: str, output_path: Path, css优化: bool = True) -> None:
    """Génère PDF depuis HTML avec WeasyPrint."""
    try:
        from weasyprint import HTML, CSS
        from weasyprint.text.fonts import FontConfiguration

        font_config = FontConfiguration()

        html = HTML(string=html_content)

        # CSS inline déjà dans le template, mais on peut ajouter surcharges
        css_overrides = None
        if css优化:
            css_overrides = CSS(string='''
                /* Optimisations ATS supplémentaires */
                body { -webkit-print-color-adjust: exact; }
            ''', font_config=font_config)

        html.write_pdf(str(output_path), stylesheets=[css_overrides] if css_overrides else None)
        print(f"✓ PDF généré : {output_path}")

    except ImportError:
        # Fallback avec autres bibliothèques
        _fallback_pdf(html_content, output_path)


def _fallback_pdf(html: str, output_path: Path) -> None:
    """Alternatives si WeasyPrint non disponible."""
    print("WeasyPrint non installé, tentatives alternatives...")

    # Option 1: pdfkit + wkhtmltopdf
    try:
        import pdfkit
        pdfkit.from_string(html, str(output_path))
        print(f"✓ PDF généré via pdfkit : {output_path}")
        return
    except Exception as e:
        print(f"  pdfkit échoué : {e}")

    # Option 2: Sauvegarde HTML pour conversion manuelle
    html_path = output_path.with_suffix('.html')
    with open(html_path, 'w', encoding='utf-8') as f:
        f.write(html)
    print(f"! HTML sauvegardé : {html_path}")
    print("  Convertissez manuellement avec Chrome (Ctrl+P → PDF) ou wkhtmltopdf")


def main():
    parser = argparse.ArgumentParser(description="Génère CV PDF depuis YAML")
    parser.add_argument('yaml_file', type=Path, help='Fichier YAML source')
    parser.add_argument('-t', '--template', type=Path, default='ATS_template.html',
                       help='Template HTML (défaut: ATS_template.html)')
    parser.add_argument('-o', '--output', type=Path, help='Fichier de sortie')
    parser.add_argument('--html-only', action='store_true', help='Génère HTML intermédiaire')
    args = parser.parse_args()

    # Résoudre chemins
    yaml_path = args.yaml_file.resolve()
    template_dir = args.template.parent.resolve()
    template_file = args.template.name

    if not yaml_path.exists():
        sys.exit(f"Erreur : fichier YAML introuvable : {yaml_path}")

    # Charger données
    cv_data = load_yaml(yaml_path)
    cv_data['input_file'] = yaml_path.name  # Métadonnées

    # Configurer Jinja2
    env = Environment(
        loader=FileSystemLoader(template_dir),
        autoescape=True,
        trim_blocks=True,
        lstrip_blocks=True,
    )
    env.filters['render_tasks'] = pass_eval_context(render_tasks)
    env.filters['format_date'] = format_month_year

    # Rendu
    template = env.get_template(template_file)
    html_output = template.render(**cv_data)

    # Sortie HTML si demandé
    if args.html_only:
        html_path = args.output or yaml_path.with_suffix('.html')
        with open(html_path, 'w', encoding='utf-8') as f:
            f.write(html_output)
        print(f"✓ HTML généré : {html_path}")
        return

    # Sortie PDF
    output_path = args.output or yaml_path.with_suffix('.pdf')
    output_path.parent.mkdir(parents=True, exist_ok=True)

    generate_pdf(html_output, output_path)

    # Méta: générer aussi version texte ATS pure
    _generate_text_version(cv_data, output_path.with_suffix('.txt'))


def _generate_text_version(data: dict, output_path: Path) -> None:
    """Version texte brute pour parsing ATS maximal."""
    lines = [
        f"{data['lastName'].upper()} {data['firstName']}",
        f"{'=' * 60}",
        f"CONTACT: {data['email']} | {data['phone']}",
        f"LOCATION: {data['address']['city']}, {data['address']['country']}",
        f"LINKEDIN: {data.get('linkedIn', 'N/A')}",
        f"",
        f"JOB TARGET: {data['jobTitle']}",
        f"NATIONALITY: {data['nationality']}",
        f"",
        f"{'=' * 60}",
        f"PROFESSIONAL EXPERIENCE",
        f"{'=' * 60}",
    ]

    for exp in reversed(data['experiences']):
        end = f"{exp['dates']['endTime']['month']:02d}/{exp['dates']['endTime']['year']}" if 'endTime' in exp.get('dates', {}) else "PRESENT"
        lines.extend([
            f"",
            f"TITLE: {exp['jobTitle']}",
            f"COMPANY: {exp['company']['name']}",
            f"PERIOD: {exp['dates']['startTime']['month']:02d}/{exp['dates']['startTime']['year']} - {end}",
            f"LOCATION: {exp['location']['city']}, {exp['location']['country']}",
            f"RESPONSIBILITIES:",
        ])
        for task in exp['tasks'].strip().split('\n'):
            clean = task.strip().replace('▪', '-').lstrip('- ')
            if clean:
                lines.append(f"  - {clean}")

    lines.extend([
        f"",
        f"{'=' * 60}",
        f"EDUCATION",
        f"{'=' * 60}",
    ])

    for edu in data['education']:
        lines.append(f"{edu['year']}: {edu['diploma']['degree']} - {edu['diploma']['name']}")
        lines.append(f"  {edu['school']['name']}, {edu['school']['location']['city']}")

    with open(output_path, 'w', encoding='utf-8') as f:
        f.write('\n'.join(lines))
    print(f"✓ Version texte ATS : {output_path}")


if __name__ == '__main__':
    main()