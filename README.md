# cantwellgraphs - open source graphing library

There are two types: Graph and PieChart<br>
You can create a graph that is a Bar Graph or a Line Graph, or both
<br>
<br>
## Graph

You can add a graph in the xml file and find it in code<br>
`Graph graph = root.findViewById(R.id.graph)`

Then, if you want a bar graph, you can add BarItems.  If you want a line graph, you can add LineItems.  If you want both bars and lines in the same chart, just add both items.

####Bar Items
`BarItem bar1 = new BarItem(4, FillType.SOLID)`<br>
The bar item constructor takes in the value and a fill type (none, solid, gradient)<br>
You can leave it as is (black line, no fill), or you can customize it (line color / width, and fill color / type)<br>

####Line Items
`LineItem line1 = new LineItem(valuesList, FilleType.GRADIENT)`<br>
The line item constructor takes in a list of value and a fill type<br>
You can leave it as is (black line, no fill), or you can add a lot of customization<br>
By default, it connects each data point with a straight line, but you can choose to make it a smooth curve<br>
`line1.setSmoothed(true)`

####User Interaction
`graph.setTouchEnabled(true)`<br>
For this to do anything useful, you need to add data highlights to line items<br>
You may add both types of data highlights to a single line item<br>
_This works best with a single item in a graph, but it does work fine with multiple items_<br>

######Vertical Highlight
The vertical highlight is a colored bar that extends the height of the graph, at a certain datapoint<br>
`VerticalHighlight vh = new VerticalHighlight(FillType.SOLID, false)`<br>
The constructor specifies a fill type, and whether or not there is a line/stroke surrounding the bar<br>
`vh.setWidth(50)`<br>
`line1.attachVerticalHighlight(vh)`<br>
This says that line1 contains a vertical highlight that will interact with user touch

######Point Highlight
The point highlight is a circle that moves to the point specified by the user<br>
It can also display a value at that point<br>
`PointHighlight ph = new PointHighlight(true, false, true)`<br>
The constructor specifies if it has fill, if it has a stroke, and if it should show the value at the data point<br>
`ph.setRadius(50)`<br>
`ph.setTextSize(40)`<br>
`ph.setStrokeColor(Color.CYAN)`<br>
`line1.attachPointHighlight(ph)`<br>
This says that line1 also contains a point highlight that will interact with user touch<br>
_By default, if you choose to show the value at the point, the point highlight will display the exact value of the datapoint.  But, you can choose to display a custom value for a datapoint._<br>

        pointHighlight.addCustomValueDisplay(new PointHighlight.ValueDisplay() {
                @Override
                public String setHighlightValue(Point p) {
                        return String.valueOf(Math.sqrt(p.value));
                }
        });
        
This will display the square root of each data point

####Displaying the Graph

After creating all of the graph items, you must add them to the graph<br>
`graph.addGraphItem(line1)`<br>
`graph.addBarItem(bar1)`<br>
There may be times you need to manually refresh the graph.  To do so, call:<br>
`graph.drawGraph()`
<br>
<br>
<br>
##Pie Chart
You can add a pie chart in the xml file and find it in code<br>
`PieChart pie = root.findViewById(R.id.pie)`

Then, you can add the sections of the pie.

####Pie Section
`PieSection p1 = new PieSection("Cherry", 25)`<br>
The constructors take a name and value for the section<br>
`p1.setFillColor(Color.RED)`<br>
`p1.setLabelType(PieLabelType.PERCENTAGE_then_NAME)`<br>
You may specify how you wish to display the value of each pie section (none, name, value, percentage, or a mix)

#### Displaying the Pie

After adding the pie items, you must add them to the pie chart<br>
`pie.addPieItem(p1)`<br>
If you wish to manually refresh the chart, call:<br>
`pie.drawChart()`
<br>
<br>
<br>
##_Coming Soon_

Specify corresponding x values for graph item values<br>
Display a legend / key<br>
More interactability<br>
Add visible points for line item data points<br>
