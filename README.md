# cantwellgraphs
open source graphing library

Example Line Graph Usage :

LineGraph graph = (LineGraph) findViewById(R.id.graph);

        // Create the list of values to display in the line graph
        List<Float> values = new ArrayList<>();
        values.add(2f);
        values.add(4.3f);
        values.add(3f);
        ...

        // Create the line to add to the graph
        // You may add multiple lines / datasets to the graph
        LineItem line = new LineItem(values, FillType.GRADIENT);
        line.setGradientFillColor(Color.WHITE, Color.BLUE);
        line.setLineColor(Color.BLUE);
        line.setSmoothed(true);

        // A vertical colored bar that moves to the datapoint selected
        VerticalHighlight verticalHighlight = new VerticalHighlight(FillType.SOLID, false);
        verticalHighlight.setSolidFillColor(Color.parseColor("#990000FF"));
        verticalHighlight.setWidth(80);

        // A circle that can highlight a datapoint and also show it's value
        PointHighlight pointHighlight = new PointHighlight(true, true, true);
        pointHighlight.setRadius(50);
        pointHighlight.setFillColor(Color.WHITE);
        pointHighlight.setTextColor(Color.BLACK);
        pointHighlight.setTextSize(30);
        pointHighlight.setStrokeColor(Color.parseColor("#990000FF"));

        // Every line can have a vertical highlight and (more useful) a data highlight
        // note: it works best when one one line have the highlights, but having multiple with highlights is perfectly fine
        line.attachVerticalHighlight(verticalHighlight);
        line.attachPointHighlight(pointHighlight);

        // Add each line item to the graph
        graph.addLineItem(line);
        // If you want to interact with the graph (use the highlights) enable touch
        graph.enableTouch(true);
        // Tell the graph to draw
        graph.drawGraph();
