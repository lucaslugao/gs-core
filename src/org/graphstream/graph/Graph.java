/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 * 
 * Copyright 2006 - 2009
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
 */

package org.graphstream.graph;

import java.io.IOException;
import java.util.Iterator;

import org.graphstream.stream.AttributeSink;
import org.graphstream.stream.ElementSink;
//import org.graphstream.stream.Sink;		// Not needed any more XXX
import org.graphstream.stream.GraphParseException;
import org.graphstream.stream.Pipe;
import org.graphstream.stream.file.FileSink;
import org.graphstream.stream.file.FileSource;
import org.graphstream.ui.old.GraphViewerRemote;

/**
 * An Interface that advises general purpose methods for handling graphs.
 * 
 * <p>
 * This interface is one of the main interfaces of GraphStream. It defines the services
 * provided by a graph structure. Graphs implementations must at least implement
 * this interface (but are free to provide more services). 
 * </p>
 *
 * <p>
 * With {@link org.graphstream.stream.Source}, {@link org.graphstream.stream.Sink}
 * and {@link org.graphstream.stream.Sink}, this interface is one of the
 * most important. A graph is a {@link org.graphstream.stream.Pipe} that buffers
 * the graph events an present the graph structure as it is actually.
 * </p> 
 * 
 * <p>
 * In other words, it allows to browse the graph structure, to explore it, to
 * modify it, and to implement algorithms on it.
 * </p>
 */
public interface Graph extends Element, Pipe, Iterable<Node>
{
// Access	

	/**
	 * Get a node by its identifier.
	 * @param id Identifier of the node to find.
	 * @return The searched node or null if not found.
	 */
	Node getNode( String id );

	/**
	 * Get an edge by its identifier.
	 * @param id Identifier of the edge to find.
	 * @return The searched edge or null if not found.
	 */
	Edge getEdge( String id );

	/**
	 * Number of nodes in this graph.
	 * @return The number of nodes.
	 */
	int getNodeCount();

	/**
	 * Number of edges in this graph.
	 * @return The number of edges.
	 */
	int getEdgeCount();

	/**
	 * Iterator on the set of node, in random order.
	 * @return The iterator.
	 */
	Iterator<? extends Node> getNodeIterator();

	/**
	 * Iterator on the set of edges, in random order.
	 * @return The iterator.
	 */
	Iterator<? extends Edge> getEdgeIterator();

	/**
	 * Set of nodes usable in a for-each instruction.
	 * @return An "iterable" view of the set of nodes.
	 * @see #getNodeIterator()
	 */
	Iterable<? extends Node> nodeSet();

	/**
	 * Set of edges usable in a for-each instruction.
	 * @return An "iterable" view of the set of edges.
	 * @see #getEdgeIterator()
	 */
	Iterable<? extends Edge> edgeSet();

	/**
	 * The factory used to create node instances.
	 * The factory can be changed to refine the node class generated for this graph.
	 * @see #setNodeFactory(NodeFactory)
	 * @see #edgeFactory()
	 */
	NodeFactory nodeFactory();
	
	/**
	 * The factory used to create edge instances.
	 * The factory can be changed to refine the edge class generated for this graph.
	 * @see #setEdgeFactory(EdgeFactory)
	 * @see #nodeFactory()
	 */
	EdgeFactory edgeFactory();

	/**
	 * Is strict checking enabled?. If strict checking is enabled the graph
	 * checks for name space conflicts (e.g. insertion of two nodes with the
	 * same name), removal of non-existing elements, use of non existing
	 * elements (create an edge between two non existing nodes). Graph implementations
	 * are free to respect strict checking or not.
	 * @return True if enabled.
	 */
	boolean isStrict();

	/**
	 * Is the automatic creation of missing elements enabled?. If enabled, when
	 * an edge is created and one or two of its nodes are not already present
	 * in the graph, the nodes are automatically created.
	 * @return True if enabled.
	 */
	boolean isAutoCreationEnabled();
	
	/**
	 * The current step.
	 * @return The step.
	 */
	double getStep();

// Command
	
	/**
	 * Set the node factory used to create nodes.
	 * @param nf the new NodeFactory
	 */
	void setNodeFactory( NodeFactory nf );
	
	/**
	 * Set the edge factory used to create edges.
	 * @param ef the new EdgeFactory
	 */
	void setEdgeFactory( EdgeFactory ef );

	/**
	 * Enable or disable strict checking.
	 * @see #isStrict()
	 * @param on True or false.
	 */
	void setStrict( boolean on );

	/**
	 * Enable or disable the automatic creation of missing elements.
	 * @see #isAutoCreationEnabled()
	 * @param on True or false.
	 */
	void setAutoCreate( boolean on );

// Graph construction
	
	/**
	 * Empties the graph completely by removing any references to nodes or edges.
	 * Every attribute is also removed. However, listeners are kept.
	 * @see #clearSinks()
	 */
	void clear();

	/**
	 * Add a node in the graph. This acts as a factory, creating the node
	 * instance automatically (and eventually using the node factory provided).
	 * An event is generated toward the listeners.
	 * If strict checking is enabled, and a node already exists with this
	 * identifier, a singleton exception is raised. Else the error is silently
	 * ignored and the already existing node is returned.
	 * @param id Arbitrary and unique string identifying the node.
	 * @return The created node (or the already existing node).
	 * @throws IdAlreadyInUseException If the identifier is already used.
	 */
	Node addNode( String id ) throws IdAlreadyInUseException;

	/**
	 * Remove the node using its identifier. An event is generated toward the
	 * listeners. Note that removing a node may remove all edges it is connected
	 * to. In this case corresponding events will also be generated toward the
	 * listeners.
	 * @param id The unique identifier of the node to remove.
	 * @return The removed node, if strict checking is disabled, it can return
	 * 	       null if the node to remove does not exist.
	 * @complexity O(1)
	 * @throws ElementNotFoundException If no node matches the given identifier.
	 */
	Node removeNode( String id ) throws ElementNotFoundException;

	/**
	 * Add an undirected edge between nodes. An event is sent toward the
	 * listeners. If strict checking is enabled and at least one of the two
	 * given nodes do not exist, a "not found" exception is raised. Else if the
	 * auto-creation feature is disabled, the error is silently ignored, and
	 * null is returned. If the auto-creation feature is enabled (see
	 * {@link #setAutoCreate(boolean)}) and one or two of the given nodes do not
	 * exist, they are automatically created.
	 * 
	 * @param id Unique an arbitrary string identifying the edge.
	 * @param node1 The first node identifier.
	 * @param node2 The second node identifier.
	 * 
	 * @return The newly created edge (this can return null, if strict checking
	 *         is disabled, auto-creation disabled, and one or two of the given
	 *         nodes do not exist).
	 * @throws IdAlreadyInUseException If an edge already exist between 'from' and 'to',
	 *         strict checking is enabled and the graph is not a multi-graph.
	 * @throws ElementNotFoundException If strict checking is enabled, and the 'from'
	 *         or 'to' node is not registered in the graph.
	 */
	Edge addEdge( String id, String node1, String node2 ) throws IdAlreadyInUseException, ElementNotFoundException;

	/**
	 * Like {@link #addEdge(String, String, String)}, but this edge can be
	 * directed between the two given nodes. If directed, the edge goes in the
	 * 'from' -&gt; 'to' direction. An event is sent toward the listeners.
	 * 
	 * @param id Unique an arbitrary string identifying the node.
	 * @param from The source node identifier.
	 * @param to The target node identifier.
	 * @param directed Is the edge directed?.
	 * 
	 * @return The newly created edge (this can return null, if strict checking
	 *         is disabled, auto-creation disabled, and one or two of the given
	 *         nodes do not exist).
	 * @throws IdAlreadyInUseException If an edge already exist between 'from' and 'to',
	 *         strict checking is enabled, and the graph is not a multi-graph.
	 * @throws ElementNotFoundException If strict checking is enabled, and the 'from'
	 *         or 'to' node is not registered in the graph.
	 */
	Edge addEdge( String id, String from, String to, boolean directed ) throws IdAlreadyInUseException, ElementNotFoundException;

	/**
	 * Remove an edge given the identifier of its two linked nodes. If the edge
	 * is directed it is removed only if its source and destination nodes are
	 * identified by 'from' and 'to' respectively. If the graph is a multi graph
	 * and there are several edges between the two nodes, one of the edge at
	 * random is removed. An event is sent toward the
	 * listeners. If strict checking is enabled and at least one of the two
	 * given nodes does not exist, a not found exception is raised. Else the
	 * error is silently ignored, and null is returned.
	 * @param from The origin node identifier to select the edge.
	 * @param to The destination node identifier to select the edge.
	 * @return The removed edge, or null if strict checking is disabled and
	 *         at least one of the two given nodes does not exist.
	 * @throws ElementNotFoundException If the 'from' or 'to' node is not registered in
	 *         the graph and strict checking is enabled.
	 */
	Edge removeEdge( String from, String to ) throws ElementNotFoundException;

	/**
	 * Remove the edge knowing its identifier. An event is sent toward the
	 * listeners. If strict checking is enabled and the edge does not exist,
	 * a not found exception is raised. Else the error is silently ignored and
	 * null is returned.
	 * @param id Identifier of the edge to remove.
	 * @return The removed edge, or null if strict checking is disabled and
	 *         the edge does not exist.
	 * @throws ElementNotFoundException If no edge matches the identifier and strict
	 *         checking is enabled.
	 */
	Edge removeEdge( String id ) throws ElementNotFoundException;

	/**
	 * <p>
	 * Since dynamic graphs are based on discrete event modifications, the notion of step is defined
	 * to simulate elapsed time between events. So a step is a event that occurs in the graph, it
	 * does not modify it but it gives a kind of timestamp that allows the tracking of the progress
	 * of the graph over the time.
	 * </p>
	 * <p>
	 * This kind of event is useful for dynamic algorithms that listen to the dynamic graph and need
	 * to measure the time in the graph's evolution.
	 * </p>
	 * 
	 * @param time
	 *            A numerical value that may give a timestamp to track the evolution of the graph
	 *            over the time.
	 */
	void stepBegins( double time );

// Source
	// XXX do we put the iterable attributeSinks and elementSinks in Source ? 
	
	/**
	 * Returns an "iterable" of {@link AttributeSink} objects registered to this graph.
	 * @return the set of {@link AttributeSink} under the form of an iterable object.
	 */
	Iterable<AttributeSink> attributeSinks();
	
	/**
	 * Returns an "iterable" of {@link ElementSink} objects registered to this graph.
	 * @return the list of {@link ElementSink} under the form of an iterable object.
	 */
	Iterable<ElementSink> elementSinks();
	
// Utility shortcuts (should be mixins or traits, what are you doing Mr Java ?)
	// XXX use a Readable/Writable/Displayable interface for this ?
	
	/**
	 * Utility method to read a graph. This method tries to identify the graph
	 * format by itself and instantiates the corresponding reader automatically.
	 * If this process fails, a NotFoundException is raised.
	 * @param filename The graph filename (or URL).
	 * @throws ElementNotFoundException If the file cannot be found or if the format is not recognised.
	 * @throws GraphParseException If there is a parsing error while reading the file.
	 * @throws IOException If an input output error occurs during the graph reading.
	 */
	void read( String filename ) throws IOException, GraphParseException, ElementNotFoundException;

	/**
	 * Utility method to read a graph using the given reader.
	 * @param input An appropriate reader for the filename.
	 * @param filename The graph filename (or URL).
	 * @throws ElementNotFoundException If the file cannot be found or if the format is not recognised.
	 * @throws GraphParseException If there is a parsing error while reading the file.
	 * @throws IOException If an input/output error occurs during the graph reading.
	 */
	void read( FileSource input, String filename ) throws IOException, GraphParseException;

	/**
	 * Utility method to write a graph in DGS format to a file.
	 * @param filename The file that will contain the saved graph (or URL).
	 * @throws IOException If an input/output error occurs during the graph writing.
	 */
	void write( String filename ) throws IOException;

	/**
	 * Utility method to write a graph in the chosen format to a file.
	 * @param filename The file that will contain the saved graph (or URL).
	 * @param output The output format to use.
	 * @throws IOException If an input/output error occurs during the graph writing.
	 */
	void write( FileSink output, String filename ) throws IOException;

	/**
	 * Utility method that create a new graph viewer, and register the graph in
	 * it. Notice that this method is a quick way to see a graph, and only this.
	 * It can be used to prototype a program, but may be limited. This method
	 * automatically launch a graph layout algorithm in its own thread to
	 * compute best node positions.
	 * @see org.graphstream.ui.old.GraphViewerRemote
	 * @see #oldDisplay(boolean)
	 * @return a graph viewer remote that allows to command the viewer (it is a remote since the
	 *         viewer often run in another thread).
	 */
	GraphViewerRemote oldDisplay();
	
	/**
	 * Utility method that create a new graph viewer, and register the graph in
	 * it. Notice that this method is a quick way to see a graph, and only this.
	 * It can be used to prototype a program, but is very limited.
	 * @param autoLayout If true a layout algorithm is launched in its own
	 *        thread to compute best node positions.
	 * @see org.graphstream.ui.old.GraphViewerRemote
	 * @see #oldDisplay()
	 * @return a graph viewer remote that allows to command the viewer (it is a remote since the
	 *         viewer often run in another thread).
	 */
	GraphViewerRemote oldDisplay( boolean autoLayout );
	
	org.graphstream.ui.swingViewer.Viewer display();
	org.graphstream.ui.swingViewer.Viewer display( boolean autoLayout );
}