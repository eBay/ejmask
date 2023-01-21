# Execute set of tools described in the bellow documentation
# https://opensource.ebay.com/contributing/approval/tooling/#repolinter

rm -rf reports
mkdir reports

mvn org.codehaus.mojo:license-maven-plugin:aggregate-third-party-report > ./reports/3pr.txt

npx snyk test --maven-aggregate-project > ./reports/snyk.txt

repolinter lint -u https://raw.githubusercontent.com/eBay/.github/main/repolinter.yaml > ./reports/repolint.txt
