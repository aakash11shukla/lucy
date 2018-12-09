## Index Writer
The **Writer** class in maven project **lucy-lucene** is used to write the index in a folder *indexedFiles* from input files stored in *inputFiles*. 

Input Files are structured documents, *JSON* files of a specific format:

    {
	  "name": "Nainital",
	  "abstract": "This place is a..,
	  "population": 248893,
	  "latlng": "(73.860275268555 29.378610610962)",
	  "isPartOf": [
	    "Nainital",
	    "Uttarakhand"
	  ],
	  "utcOffset": "+5:30",
	  "country": "India"
	}

 The Writer class picks *name, abstract, latlng, and country* and indexes these fields as TextFields and LatLonPoints.

## Index Reader

The **Searcher** class accepts query via command line argument. This class only accepts query in *JSON* format: 

    {
	    "name":"nainital",
	    "country":"india",
	    "latlng":null,
	    "abstract":null
    }
**Note:** The fields which are null are not used for search.
The result of the query is again a  *JSON Array* which is provided via standard output.

## Artifacts
The maven project **lucy-lucene** can be built into a *JAR* file with Searcher class as its main class. This jar file can then be used for querying from any platform. Given that this *JAR* and the folder *indexedFiles* are in the same directory.

## User Interface
The Web based UI is built with *Django*, it consists of an HTML page with  a search bar which accepts a query of the following format:

    abstract: city of joy; name: kolkata

The text with prefixes *'abstract', 'name', 'latlng', and 'country'* are considered for querying and everything else is neglected.

Some sample queries:

    1 - name: delhi
    2 - latlng: 26.9124 75.7873; country: india
    3 - country: united states;

The received output is then rendered as an HTML response and returned to the browser.

## How to run the project
A *JAR* file is already present in *lucy_lucene* directory, so there is no need to run the maven project.

Do not change the directory of the *JAR* since the path is already configured in Django project. 

Put your input files in *inputFiles* directory and run the following command in *lucy_lucene* directory to create the index:

    java -cp lucy.jar com.siddhantkushwaha.lucy.Writer

Once this is done, we need to run the *django* server:

    # create a virtual environment # python3.6
    python3.6 -m venv venv
    
    # activate the virtual env and install dependencies
    source venv/bin/activate
    cd lucy/lucy_django
    pip install install -r requirements.txt

    #run server
    python manage.py runserver

Visit [127.0.0.1:8000](127.0.0.1:8000)
