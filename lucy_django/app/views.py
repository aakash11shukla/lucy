from django.shortcuts import render
from app.search import get_search_results

import json


def main(request):
    return render(request, 'index.html')


def search(request):
    query = request.GET.get("query")

    query_dict = {
        'latlng': None,
        'country': None,
        'name': None,
        'abstract': None
    }

    for i in query.split(';'):
        for j in query_dict.keys():
            if j in i:
                query_dict[j] = i.replace(j + ':', '').strip()

    parsed_query = json.dumps(query_dict)
    print(parsed_query)

    result = get_search_results(parsed_query)
    return render(request, 'results.html', {"query": query, "places": result})
