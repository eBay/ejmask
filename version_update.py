##
## This script is used to update pom version
##
import re
import subprocess
import sys
from xml.etree import ElementTree as ET


## get root pom version
def get_root_pom_version():
    tree = ET.parse('./pom.xml')
    root = tree.getroot()
    for build in root:
        if (build.tag == "{http://maven.apache.org/POM/4.0.0}version"):
            return build.text
    raise ValueError("Pom must need to have a version tag.")


# get the next version
def get_next_version(current_version, version_command):
    # cleanup
    base_version = current_version.replace("-SNAPSHOT", "").replace("-RELEASE", "").replace("-PATCH", "")
    parts = base_version.split('.')
    #
    if version_command == "next-minor":
        # increment the version to next minor SNAPSHOT
        parts[-1] = str(int(parts[-1]) + 1)
        return '.'.join(parts)
    elif version_command == "next-major":
        # increment the version to next minor
        parts[1] = str(int(parts[1]) + 1)
        return '.'.join(parts)
    else:
        # manual version
        pattern = r'^\d+\.\d+\.\d+(-SNAPSHOT|-RELEASE)?$'
        if not re.match(pattern, version_command):
            raise ValueError(f"Invalid version format {version_command}")
        return version_command


def execute_maven_command(version):
    command = f"mvn versions:set -DnewVersion={version} -DgenerateBackupPoms=false"
    process = subprocess.Popen(command, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    stdout, stderr = process.communicate()
    if process.returncode != 0:
        print(f"Error executing command: {stderr.decode()}")
    else:
        print(stdout.decode())

def replace_in_specified_module(version, *modules):
    for module in modules:
        command = f"mvn versions:set -DnewVersion={version} -DgenerateBackupPoms=false -pl {module}"
        process = subprocess.Popen(command, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        stdout, stderr = process.communicate()
        if process.returncode != 0:
            print(f"Error executing command: {stderr.decode()}")
        else:
            print(stdout.decode())

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage >> python version_update.py <version> next | next-minor | next-major ")
    else:
        version_new = sys.argv[1]
        version_current = get_root_pom_version()
        version_next = get_next_version(version_current, version_new)
        print(f"Updating the pom version from {version_current} to {version_next}")
        print("Press `Y` to continue...")
        if input().lower() == 'y':
            print("Updating the version...")
            execute_maven_command(version_next)
            print("Updating the version in specified modules...")
            # please disable this line if you don't have any module
            replace_in_specified_module(version_next, "gateway-bom")
            print("Done...")
        else:
            print("Operation cancelled...")
