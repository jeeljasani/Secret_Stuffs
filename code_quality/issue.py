import subprocess

dependencies = [
    "certifi==2022.12.7",
    "charset-normalizer==3.0.1",
    "csv2md==1.1.2",
    "idna==3.4",
    "requests==2.28.2",
    "urllib3==1.26.14",
]
subprocess.run(["pip", "install", *dependencies])

import os
import sys
import requests
from csv2md import table
import csv

commit = sys.argv[1]
pat = sys.argv[2]
path_to_smells = "./report/"
smell_files = [
    "ArchitectureSmells.csv",
    "DesignSmells.csv",
    "TestSmells.csv",
    "ImplementationSmells.csv",
    "TestabilitySmells.csv",
]

headers = {
    "Authorization": f"Bearer {pat}",
    "Accept": "application/vnd.github+json",
}
URL = "https://api.github.com/repos/CSCI5308/course-project-g04/issues"

for sf in smell_files:
    with open(os.path.join(path_to_smells, sf)) as csv_file:
        list_smells = list(csv.reader(csv_file))

    raw_md = table.Table(list_smells).markdown()

    title = str(sf).replace(".csv", "") + " for commit - " + str(commit)[:7]
    body = {"title": title, "body": raw_md, "labels": ["Designite", "Smells"]}

    # Check if the issue already exists
    issues = requests.get(URL, headers=headers).json()
    create_issue = True
    for issue in issues:
        if issue["title"] == title or issue["body"] == raw_md:
            print(f"Issue already exist: {issue['html_url']}, {title} skipping")
            create_issue = False
            break

    if create_issue:
        github_output = requests.post(URL, headers=headers, json=body)
        if not github_output.status_code == 201:
            print(github_output.json())
            raise Exception
