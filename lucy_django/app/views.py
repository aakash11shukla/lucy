from django.shortcuts import render
from app.search import get_search_results


def main(request):
    return render(request, 'index.html')


def search(request):
    query = request.GET.get("query")
    result = None
    if query is not None and len(query) != 0:
        result = get_search_results(query)
    return render(request, 'results.html', {"query": query, "places": result})
