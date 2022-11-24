##
## This script is used to update pom version
##

import codecs
import os
import sys
from xml.dom.minidom import parseString
from xml.etree import ElementTree as ET


## get root pom version
def get_root_pom_version():
    tree = ET.parse('./pom.xml')
    root = tree.getroot()
    for build in root:
        if (build.tag == "{http://maven.apache.org/POM/4.0.0}version"):
            return build.text
    raise ValueError("Pom must need to have a version tag.")

## increment version
def get_next_snapshot(current_version):
    if current_version.endswith('-SNAPSHOT'):
        return current_version[:-9]
    if current_version.endswith('-RELEASE'):
        current_version = current_version.replace("-RELEASE", "")
    parts = current_version.split(".")
    return parts[0]+ '.' + str(int(parts[1]) + 1) + '.0-SNAPSHOT'

## increment version
def get_next_minor(current_version):
    if current_version.endswith('-SNAPSHOT'):
        return current_version[:-9]
    if current_version.endswith('-RELEASE'):
        current_version = current_version.replace("-RELEASE", "")
    parts = current_version.split(".")
    last_part = parts[len(parts) - 1]
    try:
        incremented_last_part = str(int(last_part) + 1)
    except TypeError:
        raise InvalidVersion("Unsuppported version format [%s]" % current_version)
    incremented_last_part = incremented_last_part.zfill(len(last_part))
    incremented_last_part = incremented_last_part + '-SNAPSHOT'
    return ".".join(parts[:-1] + [incremented_last_part])

## release version
def get_release(current_version):
    if current_version.endswith('-SNAPSHOT'):
        return current_version.replace("-SNAPSHOT", "-RELEASE")
    else:
        return current_version

## update parent pom with version id
def update_parent(fileInput, version):
    dom = parseString(read_string(fileInput))
    for version_element in get_child_elements_with_name(dom.documentElement, "version"):
        version_element.childNodes[0].nodeValue = version
    dom.writexml(open(fileInput, 'w'))

## Read string
def read_string(fileInput):
    file_obj = codecs.open(fileInput, 'r')
    byte_str = file_obj.read()
    for i in ['utf8', 'cp1252', 'ascii']:
        try:
            return byte_str.decode(i)
        except:
            pass
## Get Child element text
def get_child_text(parent, name):
    return get_child_element_with_name(parent, name).childNodes[0].nodeValue

## Get Child element name
def get_child_element_with_name(parent, name):
    for node in parent.childNodes:
        if node.localName == name:
            return node

## Get multiple child elements with name
def get_child_elements_with_name(parent, name):
    for node in parent.childNodes:
        if node.localName == name:
            yield node


##update sub module with the version id
def update_sub_module(path, version):
    pomfile = path + "/pom.xml"
    dom = parseString(read_string(pomfile))
    for parent_element in dom.getElementsByTagName('parent'):
        for version_element in parent_element.getElementsByTagName('version'):
            version_element.childNodes[0].nodeValue = version
    for parent_element in dom.getElementsByTagName('modules'):
        for version_element in parent_element.getElementsByTagName('module'):
            module = version_element.childNodes[0].nodeValue
            update_sub_module(path +'/'+ module, version)
    dom.writexml(open(pomfile, 'w'))

def update_version(version):
    ## valid
    print "@@@ Updating version to " + version
    #TODO: need to make this recursive
    dom = parseString(read_string('./pom.xml'))
    for parent_element in dom.getElementsByTagName('modules'):
        for version_element in parent_element.getElementsByTagName('module'):
            module = version_element.childNodes[0].nodeValue
            pompath = os.getcwd() + "/" + module
            print module + " -> " + version
            update_sub_module(pompath, version)
    update_parent(os.getcwd() + "/pom.xml", version)


####
if len(sys.argv) != 2:
    print "@@@ Usage >> python version_update.py <version> | next-minor (2.8.0-RELEASE - > 2.8.1-SNAPSHOT) | next-snapshot (2.8.0-RELEASE -> 2.9.0-SNAPSHOT) | release (2.9.0-SNAPSHOT -> 2.9.0-RELEASE) @@@"
else:
    version = sys.argv[1]
    version_old = get_root_pom_version()
    if (sys.argv[1] == 'next-minor'):
        version = get_next_minor(version_old)
        print "updating pom version from " + version_old + " -> " + version
    elif (sys.argv[1] == 'next-snapshot'):
        version = get_next_snapshot(version_old)
        print "updating pom version from " + version_old + " -> " + version
    elif (sys.argv[1] == 'next-release'):
        version = get_release(version_old)
        print "updating pom version from " + version_old + " -> " + version
    else:
        print "updating pom version from " + version_old + " -> " + version
        sys.stdout.write("\n@@@Press any key to continue... @@@\n")
        raw_input()

    update_version(version)
    sys.stdout.write("@@@ COMPLETED @@@\n")
