import subprocess
import json
import os


def get_search_results(query):
    path = os.getcwd()
    print(path)
    path = path.replace('lucy_django', 'lucy_lucene')
    os.chdir(path)

    process = subprocess.Popen(["java", "-jar", "lucy.jar", query], stdin=subprocess.PIPE,
                               stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    result = process.stdout.read().decode()
    process.communicate()
    return json.loads(result)


if __name__ == '__main__':
    get_search_results("city of joy")
