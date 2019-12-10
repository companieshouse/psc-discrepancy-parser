# psc-discrepancy-parser
This AWS Lambda service processes batches of CSV-format survey results (each survey gathered discrepancies with a company's Person(s) of Significant Control (PSC)), transforming a survey (in the form of a CSV record) into JSON and sending it to CHIPS.

The batches of survey results are sent by email to an address whose emails will automatically be put in an S3 bucket in a particular folder. Files arriving in that folder trigger this Lambda to run. The Lambda will attempt to read the file as an email containing a CSV attachment in a particular format, transforming each CSV record to a PscDiscrepancySurvey JSON object and sending it to CHIPS over an HTTP POST. If any of these steps fail, the file is moved to a failed-to-process folder. If all succeed, the file is moved to a successfully-processed folder.

## Installation
TODO

## Usage
There is not much usage to this. Using the Lambda amounts to installing it (see above) and causing a survey to be emailed to the pertinent email address. The Lambda will then be run automatically.

## Support
### The support process for dealing with a bug
Until [FAML-180 Make new PSC Discrepancy Contact creation idempotent](https://companieshouse.atlassian.net/browse/FAML-180) is fixed, hold off on re-processing any emails that have, for whatever reason, failed to be processed by the Lambda. Once that JIRA is fixed, dealing with an issue in the Lambda should proceed as follows:
1. Support should be alerted to an issue in the Lambda because an ERROR log was picked up by the AWS Cloudwatch for the Lambda's logs.
1. Get hold of those logs. In the logs, you will find:
  1. The AWS request ID.
  1. The S3 key, bucket, and object.
  1. The email's Message-ID, Subject, and Date headers.
1. Given these logs, you should be able to get hold of the file that failed and was moved to the S3 subfolder 'rejected' within the Lambda's S3 bucket.
1. Give those files to the dev team supporting the Lambda.
1. When the issue has been fixed, you should be able to move the whole file back into the source bucket where it will be reprocessed by the Lambda. If the Lambda has already partially processed a file and successfully sent records over to CHIPS which has, in turn, created new Contacts, then this should not matter and duplicates of those new Contacts should not be created.

There are several types of issue that may arise in the Lambda:
1. Network issues/CHIPS unreachable, causing REST calls to CHIPS to fail. The solution is, once those network issues have been fixed, to move the file from the rejected folder back to the source folder and let CHIPS sort out any duplicates.
1. A bug in the Lambda code. This will be analysed by the dev team, given the file and logs and a code fix put in place. After the fixed code has been deployed, move the file from the rejected folder back to the source folder and let CHIPS sort out any duplicates.
1. An issue with the data in the email. It may be that the email should not be processed, as it does not contain an attachment. It may be that the survey has changed and that the code should change to cope with this (so this is actually the previous issue). It may be that the data has a slight issue and a small edit would fix it. In this unlikely scenario, the data could be fixed in CSV, and emailed to the live email address, where it will end up in the S3 source folder again.

### The dev process for supporting a bug
Within this project, in the test code area, is a class called SupportUtils. This can be edited to point to a file that may contain CSV or a whole email, which should be multipart and should contain an attachment that is a BASE64-encoded CSV. It will parse these and dump them to stdout. If you have an email that has proven troublesome in the Lambda, this tool can be used to examine that email, running our actual Lambda code against it to see where it blows up.

## Design
When this Lambda runs, it attempts to process the new file in S3 as follows:
![alt text](design/LambdaActivity.svg)

## TODO:
rename MailParser to CsvExtractor
Rename PscDiscrepancySurveyCsvProcessor to CsvProcessor
Rename PscDiscrepancySurveyCsvProcessorFactory to CsvProcessorFactory
Rename PscDiscrepancySurveyCreatedListener to CsvProcessorListener