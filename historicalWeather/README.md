Historical Weather Data Graphing
by David, Emily, Gaurav, Hery

As the title suggests, the application of interest is one which will generate a graph based on the weather data that it receives. As of now, we are not sure if we will allow for
multiple types of graphs to be generated from the historical weather data; quite possibly 
focusing on a histograph, scatterplot or bar graph (as these are generally most significant when dealing with statistical data). The control and flow of the program will go as follows: 
the user will be able to input any zip code that they so desire, which will prompt our 
application to search for the weather station that is closest in vicinity to said zip code. Once
a weather station has been found, the user will be prompted to input a year of data in which
to plot, causing our application to make a GET request to receive said data and then it will
generate either a graph (either based on a pre-set graph type, or possibly giving the user the
chance to choose the type of graph they would like to display). The data that is retrieved will most likely be saved in some sort of a temporary file; in case the user would like to make a call to the same zip code and retrieve the same yearly data. By doing this, we can limit the number of calls to the weather data server and improve our application performance speed. There are a few subtleties with graphing which required us to look into a few open-source API's that can be used for graphing on the Droid OS. We have decided to go with AndroidPlot, as it seems to be a reliable open-source API for graphing as well as coming with its own tutorial. Once the graph has been displayed, the user will then be able to enter another zip code / year and begin the cycle again, or they may opt to close the program.