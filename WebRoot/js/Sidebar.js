/**
 * $Id: Sidebar.js,v 1.67 2012-07-19 19:09:23 gaudenz Exp $
 * Copyright (c) 2006-2012, JGraph Ltd
 */
/**
 * Construcs a new sidebar for the given editor.
 */
function Sidebar(editorUi, container)
{
	this.editorUi = editorUi;
	this.container = container;
	this.palettes = new Object();
	this.showTooltips = true;
	this.graph = new Graph(document.createElement('div'), null, null, this.editorUi.editor.graph.getStylesheet());
	this.graph.foldingEnabled = false;
	this.graph.autoScroll = false;
	this.graph.setTooltips(false);
	this.graph.setConnectable(false);
	this.graph.resetViewOnRootChange = false;
	this.graph.view.setTranslate(this.thumbBorder, this.thumbBorder);
	this.graph.setEnabled(false);
	mxConstants.CELL_TITLE = new Map();

	function Map() {
		 var struct = function(key, value) {  
		 	this.key = key;  
		 	this.value = value;  
		 }  
		   
		 var put = function(key, value){  
		  for (var i = 0; i < this.arr.length; i++) {  
		   if ( this.arr[i].key === key ) {  
		    this.arr[i].value = value;  
		    return;  
		   }  
		  }  
		   this.arr[this.arr.length] = new struct(key, value);  
		 }  
		   
		 var get = function(key) {  
		  for (var i = 0; i < this.arr.length; i++) {  
		   if ( this.arr[i].key === key ) {  
		     return this.arr[i].value;  
		   }  
		  }  
		  return null;  
		 }  
		   
		 var remove = function(key) {  
		  var v;  
		  for (var i = 0; i < this.arr.length; i++) {  
		   v = this.arr.pop();  
		   if ( v.key === key ) {  
		    continue;  
		   }  
		   this.arr.unshift(v);  
		  }  
		 }  
		   
		 var size = function() {  
		  return this.arr.length;  
		 }  
		   
		 var isEmpty = function() {  
		  return this.arr.length <= 0;  
		 }   
		 this.arr = new Array();  
		 this.get = get;  
		 this.put = put;  
		 this.remove = remove;  
		 this.size = size;  
		 this.isEmpty = isEmpty;  
		}

	// Workaround for VML rendering in IE8 standards mode where the container must be in the DOM
	// so that VML references can be restored via document.getElementById in mxShape.init.
	if (document.documentMode == 8)
	{
		document.body.appendChild(this.graph.container);
	}
	
	// Workaround for no rendering in 0 coordinate in FF 10
	if (this.shiftThumbs)
	{
		this.graph.view.canvas.setAttribute('transform', 'translate(1, 1)');
	}
	
	if (!mxClient.IS_TOUCH)
	{
		mxEvent.addListener(document, 'mouseup', mxUtils.bind(this, function()
		{
			this.showTooltips = true;
		}));
	
		// Enables tooltips after scroll
		mxEvent.addListener(container, 'scroll', mxUtils.bind(this, function()
		{
			this.showTooltips = true;
		}));
		
		mxEvent.addListener(document, 'mousedown', mxUtils.bind(this, function()
		{
			this.showTooltips = false;
			this.hideTooltip();
		}));

		mxEvent.addListener(document, 'mousemove', mxUtils.bind(this, function(evt)
		{
			var src = mxEvent.getSource(evt);
			
			while (src != null)
			{
				if (src == this.currentElt)
				{
					return;
				}
				
				src = src.parentNode;
			}
			
			this.hideTooltip();
		}));

		// Handles mouse leaving the window
		mxEvent.addListener(document, 'mouseout', mxUtils.bind(this, function(evt)
		{
			if (evt.toElement == null && evt.relatedTarget == null)
			{
				this.hideTooltip();
			}
		}));
	}
	
	this.init();
	
	// Pre-fetches tooltip image
	new Image().src = IMAGE_PATH + '/tooltip.png';
};

/**
 * Adds all palettes to the sidebar.
 */
Sidebar.prototype.init = function()
{
	var dir = STENCIL_PATH;
	
	this.addGeneralPalette(true);
	this.addFlatMapPalette(false);
	this.addUmlPalette(false);
	this.addBpmnPalette(dir, false);
	this.addWebServicePalette(false);
	this.addOutputPalette(false);
	//9.30检查
/*	this.addStencilPalette('flowchart', 'Flowchart', dir + '/flowchart.xml',
		';fillColor=#ffffff;strokeColor=#000000;strokeWidth=2');
	this.addStencilPalette('basic', mxResources.get('basic'), dir + '/basic.xml',
		';fillColor=#ffffff;strokeColor=#000000;strokeWidth=2');
	this.addStencilPalette('arrows', mxResources.get('arrows'), dir + '/arrows.xml',
		';fillColor=#ffffff;strokeColor=#000000;strokeWidth=2');
	this.addImagePalette('clipart', mxResources.get('clipart'), dir + '/clipart/', '_128x128.png',
		['Earth_globe', 'Empty_Folder', 'Full_Folder', 'Gear', 'Lock', 'Software', 'Virus', 'Email',
		 'Database', 'Router_Icon', 'iPad', 'iMac', 'Laptop', 'MacBook', 'Monitor_Tower', 'Printer',
		 'Server_Tower', 'Workstation', 'Firewall_02', 'Wireless_Router_N', 'Credit_Card',
		 'Piggy_Bank', 'Graph', 'Safe', 'Shopping_Cart', 'Suit1', 'Suit2', 'Suit3', 'Pilot1',
		 'Worker1', 'Soldier1', 'Doctor1', 'Tech1', 'Security1', 'Telesales1']);
*/

};

/**
 * Specifies if tooltips should be visible. Default is true.
 */
Sidebar.prototype.enableTooltips = !mxClient.IS_TOUCH;

/**
 * Shifts the thumbnail by 1 px.
 */
Sidebar.prototype.shiftThumbs = mxClient.IS_SVG || document.documentMode == 8;

/**
 * Specifies the delay for the tooltip. Default is 16 px.
 */
Sidebar.prototype.tooltipBorder = 16;

/**
 * Specifies the delay for the tooltip. Default is 2 px.
 */
Sidebar.prototype.thumbBorder = 2;

/**
 * Specifies the delay for the tooltip. Default is 300 ms.
 */
Sidebar.prototype.tooltipDelay = 300;

/**
 * Specifies if edges should be used as templates if clicked. Default is true.
 */
Sidebar.prototype.installEdges = true;

/**
 * Specifies the URL of the gear image.
 */
Sidebar.prototype.gearImage = STENCIL_PATH + '/clipart/Gear_128x128.png';

/**
 * Specifies the width of the thumbnails.
 */
Sidebar.prototype.thumbWidth = 26;

/**
 * Specifies the height of the thumbnails.
 */
Sidebar.prototype.thumbHeight = 26;

/**
 * Adds all palettes to the sidebar.
 */
Sidebar.prototype.showTooltip = function(elt, cells)
{
	if (this.enableTooltips && this.showTooltips)
	{
		if (this.currentElt != elt)
		{
			if (this.thread != null)
			{
				window.clearTimeout(this.thread);
				this.thread = null;
			}
			
			var show = mxUtils.bind(this, function()
			{
				// Workaround for off-screen text rendering in IE
				var old = mxText.prototype.getTableSize;
				
				if (this.graph.dialect != mxConstants.DIALECT_SVG)
				{
					mxText.prototype.getTableSize = function(table)
					{
						var oldParent = table.parentNode;
						
						document.body.appendChild(table);
						var size = new mxRectangle(0, 0, table.offsetWidth, table.offsetHeight);
						oldParent.appendChild(table);
						
						return size;
					};
				}
				
				// Lazy creation of the DOM nodes and graph instance
				if (this.tooltip == null)
				{
					this.tooltip = document.createElement('div');
					this.tooltip.className = 'geSidebarTooltip';
					document.body.appendChild(this.tooltip);

					this.tooltipsTitle = document.createElement('div');
					this.tooltipsTitle.className="tooltipsTitle";
					this.tooltipsTitle.innerHTML="<p>hello</p>";
					this.tooltip.appendChild(this.tooltipsTitle);

					
					this.graph2 = new Graph(this.tooltip, null, null, this.editorUi.editor.graph.getStylesheet());
					this.graph2.view.setTranslate(this.tooltipBorder, this.tooltipBorder);
					this.graph2.resetViewOnRootChange = false;
					this.graph2.foldingEnabled = false;
					this.graph2.autoScroll = false;
					this.graph2.setTooltips(false);
					this.graph2.setConnectable(false);
					this.graph2.setEnabled(false);
					
					this.tooltipImage = mxUtils.createImage(IMAGE_PATH + '/tooltip.png');
					this.tooltipImage.style.position = 'absolute';
					this.tooltipImage.style.width = '14px';
					this.tooltipImage.style.height = '27px';
					
					document.body.appendChild(this.tooltipImage);			


				}
				
				this.graph2.model.clear();
				this.graph2.addCells(cells);
				
				var bounds = this.graph2.getGraphBounds();
				var width = bounds.x + bounds.width + this.tooltipBorder;
				var height = bounds.y + bounds.height + this.tooltipBorder;
				
				if (mxClient.IS_QUIRKS)
				{
					width += 4;
					height += 4;
				}
				
				this.tooltipsTitle.innerHTML="<p>"+mxConstants.CELL_TITLE.get(cells[0].style)+"</p>";
				this.tooltip.style.display = 'block';
				this.tooltip.style.overflow = 'visible';
				this.tooltipImage.style.visibility = 'visible';
				this.tooltip.style.width = width + 'px';
				this.tooltip.style.height = height+40 + 'px';
		
				var left = this.container.clientWidth + this.editorUi.splitSize + 3;
				var top = Math.max(0, (this.container.offsetTop + elt.offsetTop - this.container.scrollTop - height / 2 + 16));

				// Workaround for ignored position CSS style in IE9
				// (changes to relative without the following line)
				this.tooltip.style.position = 'absolute';
				this.tooltip.style.left = left + 'px';
				this.tooltip.style.top = top + 'px';
				this.tooltipImage.style.left = (left - 13) + 'px';
				this.tooltipImage.style.top = (top + height / 2 - 13) + 'px';
				
				mxText.prototype.getTableSize = old;

			});

			if (this.tooltip != null && this.tooltip.style.display != 'none')
			{
				show();
			}
			else
			{
				this.thread = window.setTimeout(show, this.tooltipDelay);
			}

			this.currentElt = elt;
		}
	}
};

/**
 * Hides the current tooltip.
 */
Sidebar.prototype.hideTooltip = function()
{
	if (this.thread != null)
	{
		window.clearTimeout(this.thread);
		this.thread = null;
	}
	
	if (this.tooltip != null)
	{
		this.tooltip.style.display = 'none';
		this.tooltipImage.style.visibility = 'hidden';
		this.currentElt = null;
	}
};

/**
 * Adds the general palette to the sidebar.
 */
Sidebar.prototype.addGeneralPalette = function(expand)
{
	this.addPalette('general', '数据源', expand || true, mxUtils.bind(this, function(content)
	{
	    content.appendChild(this.createVertexTemplate('ellipse', 80, 80));
	    mxConstants.CELL_TITLE.put("ellipse","数据源");
	    content.appendChild(this.createVertexTemplate('shape=hexagon', 120, 80));
	    mxConstants.CELL_TITLE.put("shape=hexagon","数据适配器");
	}));
};

Sidebar.prototype.addFlatMapPalette = function(expand)
{
	this.addPalette('general', '分束/过滤', expand || false, mxUtils.bind(this, function(content)
	{
	    content.appendChild(this.createVertexTemplate('shape=step', 120, 80));
	    mxConstants.CELL_TITLE.put("shape=step","分束器");
	    content.appendChild(this.createVertexTemplate('shape=xor', 60, 80));
	    mxConstants.CELL_TITLE.put("shape=xor","filter");
	}));
};

/**
 * Adds the general palette to the sidebar.
 */
Sidebar.prototype.addUmlPalette = function(expand)
{
	this.addPalette('uml', 'map', expand || false, mxUtils.bind(this, function(content)
	{	
    	
    content.appendChild(this.createVertexTemplate('rhombus', 80, 80));
	 mxConstants.CELL_TITLE.put("rhombus","map");

	 // content.appendChild(this.createVertexTemplate('', 110, 50, 'Object'));
    	
	 //    var classCell = new mxCell('<p style="margin:0px;margin-top:4px;text-align:center;">' +
  //   			'<b>Class</b></p>' +
		// 		'<hr/><div style="height:2px;"></div><hr/>', new mxGeometry(0, 0, 140, 60),
		// 		'verticalAlign=top;align=left;overflow=fill;fontSize=12;fontFamily=Helvetica;html=1');
  //   	classCell.vertex = true;

  //   	content.appendChild(this.createVertexTemplateFromCells([classCell], 140, 60));
    	
	 //    var classCell = new mxCell('<p style="margin:0px;margin-top:4px;text-align:center;">' +
  //   			'<b>Class</b></p>' +
		// 		'<hr/><p style="margin:0px;margin-left:4px;">+ field: Type</p><hr/>' +
		// 		'<p style="margin:0px;margin-left:4px;">+ method(): Type</p>', new mxGeometry(0, 0, 160, 90),
		// 		'verticalAlign=top;align=left;overflow=fill;fontSize=12;fontFamily=Helvetica;html=1');
  //   	classCell.vertex = true;

  //   	content.appendChild(this.createVertexTemplateFromCells([classCell], 160, 90));
    	
	 //    var classCell = new mxCell('<p style="margin:0px;margin-top:4px;text-align:center;">' +
  //   			'<i>&lt;&lt;Interface&gt;&gt;</i><br/><b>Interface</b></p>' +
		// 		'<hr/><p style="margin:0px;margin-left:4px;">+ field1: Type<br/>' +
		// 		'+ field2: Type</p>' +
		// 		'<hr/><p style="margin:0px;margin-left:4px;">' +
		// 		'+ method1(Type): Type<br/>' +
		// 		'+ method2(Type, Type): Type</p>', new mxGeometry(0, 0, 190, 140),
		// 		'verticalAlign=top;align=left;overflow=fill;fontSize=12;fontFamily=Helvetica;html=1');
  //   	classCell.vertex = true;
	}));
};

/**
 * Adds the BPMN library to the sidebar.
 */
Sidebar.prototype.addBpmnPalette = function(dir, expand)
{
	this.addPalette('bpmn', 'reduce', expand || false, mxUtils.bind(this, function(content)
	{	
    	
    	content.appendChild(this.createVertexTemplate('triangle', 60, 80));
    	 mxConstants.CELL_TITLE.put("triangle","reduce");
	    
	}));
};

/**
 * Adds the webService library to the sidebar.
 */
Sidebar.prototype.addWebServicePalette = function(dir, expand)
{
	this.addPalette('bpmn', 'web service', expand || false, mxUtils.bind(this, function(content)
	{	
    	
    	content.appendChild(this.createVertexTemplate('ellipse;shape=cloud', 120, 80));
    	mxConstants.CELL_TITLE.put("ellipse;shape=cloud","webService");
	}));
};

/**
 * Adds the output library to the sidebar.
 */
Sidebar.prototype.addOutputPalette = function(expand)
{
	this.addPalette('output', 'output', expand || false, mxUtils.bind(this, function(content)
	{	
    	
    	content.appendChild(this.createVertexTemplate('ellipse;shape=doubleEllipse', 80, 80));
	 mxConstants.CELL_TITLE.put("ellipse;shape=doubleEllipse","数据输出");

	}));
};

/**
 * Creates and returns the given title element.
 */
Sidebar.prototype.createTitle = function(label)
{
	var elt = document.createElement('a');
	elt.setAttribute('href', 'javascript:void(0);');
	elt.className = 'geTitle';
	mxUtils.write(elt, label);

	return elt;
};

/**
 * Creates a thumbnail for the given cells.
 */
Sidebar.prototype.createThumb = function(cells, width, height, parent)
{
	// Workaround for off-screen text rendering in IE
	var old = mxText.prototype.getTableSize;
	
	if (this.graph.dialect != mxConstants.DIALECT_SVG)
	{
		mxText.prototype.getTableSize = function(table)
		{
			var oldParent = table.parentNode;
			
			document.body.appendChild(table);
			var size = new mxRectangle(0, 0, table.offsetWidth, table.offsetHeight);
			oldParent.appendChild(table);
			
			return size;
		};
	}
	
	var prev = mxImageShape.prototype.preserveImageAspect;
	mxImageShape.prototype.preserveImageAspect = false;
	
	this.graph.view.rendering = false;
	this.graph.view.setScale(1);
	this.graph.addCells(cells);
	var bounds = this.graph.getGraphBounds();

	var corr = (this.shiftThumbs) ? this.thumbBorder + 1 : this.thumbBorder;
	var s = Math.min((width - 1) / (bounds.x + bounds.width + corr),
		(height - 1) / (bounds.y + bounds.height + corr));
	this.graph.view.setScale(s);
	this.graph.view.rendering = true;
	this.graph.refresh();
	mxImageShape.prototype.preserveImageAspect = prev;

	bounds = this.graph.getGraphBounds();
	var dx = Math.max(0, Math.floor((width - bounds.width) / 2));
	var dy = Math.max(0, Math.floor((height - bounds.height) / 2));
	
	var node = null;
	
	// For supporting HTML labels in IE9 standards mode the container is cloned instead
	if (this.graph.dialect == mxConstants.DIALECT_SVG && !mxClient.IS_IE)
	{
		node = this.graph.view.getCanvas().ownerSVGElement.cloneNode(true);
	}
	// Workaround for VML rendering in IE8 standards mode
	else if (document.documentMode == 8)
	{
		node = this.graph.container.cloneNode(false);
		node.innerHTML = this.graph.container.innerHTML;
	}
	else
	{
		node = this.graph.container.cloneNode(true);
	}
	
	this.graph.getModel().clear();
	
	// Outer dimension is (32, 32)
	var dd = (this.shiftThumbs) ? 2 : 3;
	node.style.position = 'relative';
	node.style.overflow = 'visible';
	node.style.cursor = 'pointer';
	node.style.left = (dx + dd) + 'px';
	node.style.top = (dy + dd) + 'px';
	node.style.width = width + 'px';
	node.style.height = height + 'px';
	
	parent.appendChild(node);
	mxText.prototype.getTableSize = old;
};

/**
 * Creates and returns a new palette item for the given image.
 */
Sidebar.prototype.createItem = function(cells)
{
	var elt = document.createElement('a');
	elt.setAttribute('href', 'javascript:void(0);');
	elt.className = 'geItem';
	
	// Blocks default click action
	mxEvent.addListener(elt, 'click', function(evt)
	{
		mxEvent.consume(evt);
	});

	this.createThumb(cells, this.thumbWidth, this.thumbHeight, elt);
	
	return elt;
};

/**
 * Creates a drop handler for inserting the given cells.
 */
Sidebar.prototype.createDropHandler = function(cells, allowSplit)
{
	return function(graph, evt, target, x, y)
	{
		cells = graph.getImportableCells(cells);
		
		if (cells.length > 0)
		{
			var validDropTarget = (target != null) ?
				graph.isValidDropTarget(target, cells, evt) : false;
			var select = null;
			
			if (target != null && !validDropTarget)
			{
				target = null;
			}
			
			// Splits the target edge or inserts into target group
			if (allowSplit && graph.isSplitEnabled() && graph.isSplitTarget(target, cells, evt))
			{
				graph.splitEdge(target, cells, null, x, y);
				select = cells;
			}
			else if (cells.length > 0)
			{
				select = graph.importCells(cells, x, y, target);
			}
			
			if (select != null && select.length > 0)
			{
				graph.scrollCellToVisible(select[0]);
				graph.setSelectionCells(select);
			}
		}
	};
};

/**
 * Creates and returns a preview element for the given width and height.
 */
Sidebar.prototype.createDragPreview = function(width, height)
{
	var elt = document.createElement('div');
	elt.style.border = '1px dashed black';
	elt.style.width = width + 'px';
	elt.style.height = height + 'px';
	
	return elt;
};

/**
 * Creates a drag source for the given element.
 */
Sidebar.prototype.createDragSource = function(elt, dropHandler, preview)
{
	var dragSource = mxUtils.makeDraggable(elt, this.editorUi.editor.graph, dropHandler,
		preview, 0, 0, this.editorUi.editor.graph.autoscroll, true, true);

	// Allows drop into cell only if target is a valid root
	dragSource.getDropTarget = function(graph, x, y)
	{
		var target = mxDragSource.prototype.getDropTarget.apply(this, arguments);
		
		if (!graph.isValidRoot(target))
		{
			target = null;
		}
		
		return target;
	};
	
	return dragSource;
};

/**
 * Adds a handler for inserting the cell with a single click.
 */
Sidebar.prototype.addClickHandler = function(elt, ds)
{
	var graph = this.editorUi.editor.graph;
	var first = null;
	
	var md = (mxClient.IS_TOUCH) ? 'touchstart' : 'mousedown';
	mxEvent.addListener(elt, md, function(evt)
	{
		first = new mxPoint(mxEvent.getClientX(evt), mxEvent.getClientY(evt));
	});
	
	var oldMouseUp = ds.mouseUp;
	ds.mouseUp = function(evt)
	{
		if (!mxEvent.isPopupTrigger(evt) && this.currentGraph == null && first != null)
		{
			var tol = graph.tolerance;
			
			if (Math.abs(first.x - mxEvent.getClientX(evt)) <= tol &&
				Math.abs(first.y - mxEvent.getClientY(evt)) <= tol)
			{
				var gs = graph.getGridSize();
				ds.drop(graph, evt, null, gs, gs);
			}
		}

		oldMouseUp.apply(this, arguments);
		first = null;
	};
};

/**
 * Creates a drop handler for inserting the given cells.
 */
Sidebar.prototype.createVertexTemplate = function(style, width, height, value)
{
	var cells = [new mxCell((value != null) ? value : '', new mxGeometry(0, 0, width, height), style)];
	cells[0].vertex = true;
	
	return this.createVertexTemplateFromCells(cells, width, height);
};

/**
 * Creates a drop handler for inserting the given cells.
 */
Sidebar.prototype.createVertexTemplateFromCells = function(cells, width, height)
{
	var elt = this.createItem(cells);
	var ds = this.createDragSource(elt, this.createDropHandler(cells, true), this.createDragPreview(width, height));
	this.addClickHandler(elt, ds);

	// Uses guides for vertices only if enabled in graph
	ds.isGuidesEnabled = mxUtils.bind(this, function()
	{
		return this.editorUi.editor.graph.graphHandler.guidesEnabled;
	});

	// Shows a tooltip with the rendered cell
	if (!touchStyle)
	{
		mxEvent.addListener(elt, 'mousemove', mxUtils.bind(this, function(evt)
		{
			this.showTooltip(elt, cells);
		}));
	}
	
	return elt;
};

/**
 * Creates a drop handler for inserting the given cells.
 */
Sidebar.prototype.createEdgeTemplate = function(style, width, height, value)
{
	var cells = [new mxCell((value != null) ? value : '', new mxGeometry(0, 0, width, height), style)];
	cells[0].geometry.setTerminalPoint(new mxPoint(0, height), true);
	cells[0].geometry.setTerminalPoint(new mxPoint(width, 0), false);
	cells[0].edge = true;
	
	return this.createEdgeTemplateFromCells(cells, width, height);
};

/**
 * Creates a drop handler for inserting the given cells.
 */
Sidebar.prototype.createEdgeTemplateFromCells = function(cells, width, height)
{
	var elt = this.createItem(cells);
	this.createDragSource(elt, this.createDropHandler(cells, false), this.createDragPreview(width, height));

	// Installs the default edge
	var graph = this.editorUi.editor.graph;
	mxEvent.addListener(elt, 'click', mxUtils.bind(this, function(evt)
	{
		if (this.installEdges)
		{
			// Uses edge template for connect preview
			graph.connectionHandler.createEdgeState = function(me)
			{
	    		return graph.view.createState(cells[0]);
		    };
	
		    // Creates new connections from edge template
		    graph.connectionHandler.factoryMethod = function()
		    {
	    		return graph.cloneCells([cells[0]])[0];
		    };
		}
		
		// Highlights the entry for 200ms
		elt.style.backgroundColor = '#ffffff';
		
		window.setTimeout(function()
		{
			elt.style.backgroundColor = '';
		}, 200);
	    
	    mxEvent.consume(evt);
	}));

	// Shows a tooltip with the rendered cell
	if (!touchStyle)
	{
		mxEvent.addListener(elt, 'mousemove', mxUtils.bind(this, function(evt)
		{
			this.showTooltip(elt, cells);
		}));
	}
	
	return elt;
};

/**
 * Adds the given palette.
 */
Sidebar.prototype.addPalette = function(id, title, expanded, onInit)
{
	var elt = this.createTitle(title);
	this.container.appendChild(elt);
	
	var div = document.createElement('div');
	div.className = 'geSidebar';
	
	if (expanded)
	{
		onInit(div);
		onInit = null;
	}
	else
	{
		div.style.display = 'none';
	}
	
    this.addFoldingHandler(elt, div, onInit);
	
	var outer = document.createElement('div');
    outer.appendChild(div);
    this.container.appendChild(outer);
    
    // Keeps references to the DOM nodes
    if (id != null)
    {
    	this.palettes[id] = [elt, outer];
    }
};

/**
 * Create the given title element.
 */
Sidebar.prototype.addFoldingHandler = function(title, content, funct)
{
	var initialized = false;

	title.style.backgroundImage = (content.style.display == 'none') ?
		'url(' + IMAGE_PATH + '/collapsed.gif)' : 'url(' + IMAGE_PATH + '/expanded.gif)';
	title.style.backgroundRepeat = 'no-repeat';
	title.style.backgroundPosition = '100% 50%';
	
	mxEvent.addListener(title, 'click', function(evt)
	{
		if (content.style.display == 'none')
		{
			if (!initialized)
			{
				initialized = true;
				
				if (funct != null)
				{
					funct(content);
				}
			}
			
			title.style.backgroundImage = 'url(' + IMAGE_PATH + '/expanded.gif)';
			content.style.display = 'block';
		}
		else
		{
			title.style.backgroundImage = 'url(' + IMAGE_PATH + '/collapsed.gif)';
			content.style.display = 'none';
		}
		
		mxEvent.consume(evt);
	});
};

/**
 * Removes the palette for the given ID.
 */
Sidebar.prototype.removePalette = function(id)
{
	var elts = this.palettes[id];
	
	if (elts != null)
	{
		this.palettes[id] = null;
		
		for (var i = 0; i < elts.length; i++)
		{
			this.container.removeChild(elts[i]);
		}
		
		return true;
	}
	
	return false;
};

/**
 * Adds the given image palette.
 */
Sidebar.prototype.addImagePalette = function(id, title, prefix, postfix, items)
{
	this.addPalette(id, title, false, mxUtils.bind(this, function(content)
    {
    	for (var i = 0; i < items.length; i++)
		{
			var icon = prefix + items[i] + postfix;
			content.appendChild(this.createVertexTemplate('image;image=' + icon, 80, 80, ''));
		}
    }));
};

/**
 * Adds the given stencil palette.
 */
Sidebar.prototype.addStencilPalette = function(id, title, stencilFile, style, ignore, onInit, scale)
{
	scale = (scale != null) ? scale : 1;
	
	this.addPalette(id, title, false, mxUtils.bind(this, function(content)
    {
		if (style == null)
		{
			style = '';
		}
		
		if (onInit != null)
		{
			onInit.call(this, content);
		}

		mxStencilRegistry.loadStencilSet(stencilFile, mxUtils.bind(this, function(packageName, stencilName, displayName, w, h)
		{
			if (ignore == null || mxUtils.indexOf(ignore, stencilName) < 0)
			{
				content.appendChild(this.createVertexTemplate('shape=' + packageName + stencilName.toLowerCase() + style,
					Math.round(w * scale), Math.round(h * scale), ''));
			}
		}), true);
    }));
};
