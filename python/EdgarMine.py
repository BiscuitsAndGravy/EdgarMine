from html.parser import HTMLParser
import urllib.request as urllib2
import csv
import sys

class MyHTMLParser(HTMLParser):
    lsData = list()

    def handle_data(self, data):
        self.lsData.append(data)

def main():
    parser = MyHTMLParser()

    officerCIK = '0001218363'
    if len(sys.argv) > 1:
        officerCIK = sys.argv[2]

    officerURL = 'https://www.sec.gov/cgi-bin/own-disp?action=getowner&CIK=' + officerCIK + '&type=&dateb=&owner=include&start=0'
    html_page = html_page = urllib2.urlopen(officerURL)

    parser.feed(str(html_page.read()))
    bodyText = str(parser.lsData)
    startIndex = bodyText.find('Ownership Information: ') + 23
    endIndex = bodyText[startIndex:].find(', ') + startIndex - 1
    officerName = bodyText[startIndex:endIndex] + '.csv'

    startIndex = bodyText.find('Security Name')
    bodyList = bodyText[startIndex+37:].split(', ')

    securityType = list()
    transactionDate = list()
    transactionAmount = list()
    securitiesOwned = list()
    securityIssuer = list()

    for i in range(0, len(bodyList), 26):
        if i+22 >= len(bodyList):
            break
        securityType.append(bodyList[i+22])
        transactionDate.append(bodyList[i+2])
        transactionAmount.append(bodyList[i+14])
        securitiesOwned.append(bodyList[i+16])
        securityIssuer.append(bodyList[i+6])

    with open(officerName, 'w') as f:
        for i in range(len(securityType)):
            f.write(transactionDate[i])
            f.write(',')
            f.write(securityIssuer[i])
            f.write(',')
            f.write(securityType[i])
            f.write(',')
            f.write(transactionAmount[i])
            f.write(',')
            f.write(securitiesOwned[i])
            f.write('\n')

if __name__ == "__main__":
    main()