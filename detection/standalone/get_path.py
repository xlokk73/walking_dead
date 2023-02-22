import re

def get_path(line):
    pattern = r'^package:([^\s]+)='


    # Use regular expressions to extract the file path from the line
    match = re.search(pattern, line)
    if match:
        file_path = match.group(1)
        print(file_path)

    return file_path


line = "package:/data/app/com.example.dynamiccodeloadingexample-kM_T4gA7zGJ6O0paRChLMw==/base.apk=com.example.dynamiccodeloadingexample"
get_path(line)
