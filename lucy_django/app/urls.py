from django.urls import path

from app import views

app_name = 'app'

urlpatterns = [
    path(r'', views.main, name='main'),
    path(r'search/', views.search, name='search'),
]
