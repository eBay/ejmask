import xml.etree.ElementTree as ET
import sys
import xml.dom.minidom as minidom
import re

pom_file = 'pom.xml'

"""
This script updates the version information in a Maven POM file.

Usage:
    python version_update.py <version_type>

Options:
    - major    : (2.8.0 -> 3.0.0)
    - minor    : (2.8.0 -> 2.9.0-SNAPSHOT)
    - patch    : (2.8.0 -> 2.8.1-SNAPSHOT)
    - release  : (2.9.0-SNAPSHOT -> 2.9.0)
"""

def read_pom_properties():
    """
    Reads the revision and changelist properties from the POM file.

    Args:
        pom_file (str): Path to the POM file.

    Returns:
        tuple: A tuple containing the revision and changelist values.

    Example:
        revision, changelist = read_pom_properties('pom.xml')
    """
    tree = ET.parse(pom_file)
    root = tree.getroot()
    namespace = {'mvn': 'http://maven.apache.org/POM/4.0.0'}
    revision = root.find('mvn:properties/mvn:revision', namespace).text
    changelist = root.find('mvn:properties/mvn:changelist', namespace).text
    return revision, changelist

def get_version(revision, changelist, version_type):
    """
    Generates the new version based on the version type.

    Args:
        revision (str): The current revision.
        changelist (str): The current changelist.
        version_type (str): The type of version update ('major', 'minor', 'patch', 'release').

    Returns:
        tuple: A tuple containing the new revision and changelist values.

    Example:
        new_revision, new_changelist = get_version('2.8.0', '-SNAPSHOT', 'minor')
    """
    parts = revision.split('.')
    major, minor, patch = int(parts[0]), int(parts[1]), int(parts[2])
    if version_type == 'major':
        major += 1
        minor = 0
        patch = 0
        changelist = '-SNAPSHOT'
    elif version_type == 'minor':
        minor += 1
        patch = 0
        changelist = '-SNAPSHOT'
    elif version_type == 'patch':
        patch += 1
        changelist = '-SNAPSHOT'
    elif version_type == 'release' and changelist == '-SNAPSHOT':
        changelist = ''
    new_revision = f"{major}.{minor}.{patch}"
    return new_revision, changelist

def print_help():
    print("Usage >: python version_update.py <version> \n options: "
          "\n - major    : (2.8.0 -> 3.0.0-SNAPSHOT)"
          "\n - minor    : (2.8.0 -> 2.1.0-SNAPSHOT)"
          "\n - patch    : (2.8.0 -> 2.8.1-SNAPSHOT)"
          "\n - release  : (2.9.0-SNAPSHOT -> 2.9.0)")

def update_pom(version_old, revision, changelist):
    """
    Updates the POM file with the new version information.

    Args:
        version_old (str): The old version string.
        revision (str): The new revision.
        changelist (str): The new changelist.
        pom_file (str): Path to the POM file.

    Example:
        update_pom('2.8.0-SNAPSHOT', '2.8.1', '', 'pom.xml')
    """
    print(f"updating pom version from {version_old} -> {revision}{changelist}")
    if input("Do you want to update the pom version (y/n): ").lower() != 'y':
        sys.exit(0)
    dom = minidom.parse(pom_file)
    properties = dom.getElementsByTagName('properties')[0]
    revision_elements = dom.getElementsByTagName('revision')
    changelist_elements = dom.getElementsByTagName('changelist')

    if revision_elements and revision_elements[0].firstChild:
        revision_elements[0].firstChild.nodeValue = revision
    else:
        new_revision = dom.createElement('revision')
        new_revision.appendChild(dom.createTextNode(revision))
        properties.appendChild(new_revision)

    if changelist_elements and changelist_elements[0].firstChild:
        changelist_elements[0].firstChild.nodeValue = changelist
    else:
        for elem in changelist_elements:
            if not elem.firstChild or not elem.firstChild.nodeValue.strip():
                properties.removeChild(elem)
        new_changelist = dom.createElement('changelist')
        new_changelist.appendChild(dom.createTextNode(changelist))
        properties.appendChild(new_changelist)

    with open(pom_file, 'w') as f:
        dom.writexml(f)

def update_readme(new_version, updated_changelist):
    if(updated_changelist == '-SNAPSHOT'):
        return

    # Read the content of the README.md file
    with open('README.md', 'r') as file:
        content = file.read()

    # Define the regex patterns for Maven and Gradle version updates
    maven_pattern = r'(<version>)(\d+\.\d+\.\d+)(</version>)'
    gradle_pattern = r"(version: ')(\d+\.\d+\.\d+)(')"

    # Replace the old version with the new version
    updated_content = re.sub(maven_pattern, r'\g<1>' + new_version + r'\g<3>', content)
    updated_content = re.sub(gradle_pattern, r'\g<1>' + new_version + r'\g<3>', updated_content)

    # Write the updated content back to the README.md file
    with open('README.md', 'w') as file:
        file.write(updated_content)

    print("Version updated successfully.")

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print_help()
    else:
        revision, changelist = read_pom_properties()
        changelist = changelist or ''
        version_old = revision + changelist
        version_type = sys.argv[1]
        if version_type in ['major', 'minor', 'patch', 'release']:
            uprated_revision, updated_changelist = get_version(revision, changelist, version_type)
            update_pom(version_old, uprated_revision, updated_changelist)
            update_readme(uprated_revision, updated_changelist)
        else:
            print_help()