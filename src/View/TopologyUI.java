/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package View;

import Model.PeerNode;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Adam
 */
public class TopologyUI extends javax.swing.JPanel implements MouseMotionListener, MouseListener {

    public final static int CIRCLEDIAMETER = 30; //Diameter of the nodes
    private int numNodes;
    private ArrayList<TopologyUI.Node> adjList; //Nodes are stored in an ArrayList
    private final int barb; //size of an arrow edge
    private final double phi; //angle of an arrow edge
    private int moveNode; //the node the user is moving on the GUI
    private int startNode; //user input starting node for traversals
    private PeerNode node;
    
    
    /**
     * Creates new form TopologyUI
     */
    public TopologyUI(PeerNode n) {
        initComponents();
        setSize(500,500);
        this.node = n;
        Map<String, PeerNode> nodeList = node.getPeers();
        Set<Map.Entry<String, PeerNode>> setList = nodeList.entrySet();
        Iterator it = setList.iterator();
        numNodes = setList.size();
        adjList = new ArrayList<>();
        System.out.println("num Nodes " + numNodes);
        
        int pos = 0;
        for (Map.Entry<String, PeerNode> e : setList)
        {
            TopologyUI.Node newNode = new TopologyUI.Node();
            newNode.nodeName = e.getKey();
            System.out.println("New node " + newNode.nodeName);
            adjList.add(newNode);
            newNode.edges = new int[nodeList.size()];
            newNode.nodeNum=pos;
            System.out.println("added node at pos " + pos);
            pos++;
        }
            
         //set the nodes position randomly
        int xinc= (int)(Math.random()*300);
        int yinc= (int)(Math.random()*300);
        boolean swap =false;
        for(TopologyUI.Node node : adjList){
            node.xpos=(int)(Math.random()*300);
            node.ypos=(int)(Math.random()*300);
            node.circleCenter = new Point(xinc+(CIRCLEDIAMETER),yinc+(CIRCLEDIAMETER/2));
        }
        moveNode=-1;
        startNode= 0;
        barb = 20;                   // barb length
        phi = Math.PI/12;             // 30 degrees barb angle
        setBackground(Color.white);
        addMouseMotionListener(this);
        addMouseListener(this);
    }
        
    
    @Override
    public void paintComponent(Graphics g)
    {
        //clear the previous screen
       	g.setColor(Color.WHITE);
       	g.fillRect(0,0,1500,1500);
       	g.setColor(Color.BLACK);
        

    	//draws the edges, calls method edge for each edge to be drawn
        Node n;
     	for(int j=0; j < adjList.size(); j++){
            n = adjList.get(j);
            n.draw(g);
            for(int i=0; i < n.edges.length; i++){
                edge(n.nodeNum,adjList.get(n.edges[i]).nodeNum,g);
            }
        }

    }


    /**
     * draws an edge between two nodes.
     * @param node1
     * @param node2
     * @param g 
     */
    public void edge(int node1, int node2, Graphics g){
        int startX = adjList.get(node1).circleCenter.x;
        int startY = adjList.get(node1).circleCenter.y;

        int destX = adjList.get(node2).circleCenter.x;
        int destY = adjList.get(node2).circleCenter.y;

        g.setColor(Color.BLACK);

        g.drawLine(startX,startY,destX,destY);
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                         RenderingHints.VALUE_ANTIALIAS_ON);
        double theta, x, y;
        g2.setPaint(Color.blue);
        theta = Math.atan2(destY - startY, destX - startX);
        drawArrow(g2, theta, destX, destY);

     }

    /**
     * draws the arrows on the edges
     * @param g2
     * @param theta
     * @param x0
     * @param y0 
     */
    private void drawArrow(Graphics2D g2, double theta, double x0, double y0){
        double x = x0 - barb * Math.cos(theta + phi);
        double y = y0 - barb * Math.sin(theta + phi);
        g2.draw(new Line2D.Double(x0, y0, x, y));
        x = x0 - barb * Math.cos(theta - phi);
        y = y0 - barb * Math.sin(theta - phi);
        g2.draw(new Line2D.Double(x0, y0, x, y));
    }


    /**
     * Mouse Actions
     * @param e 
     */
    @Override
    public void mouseDragged(MouseEvent e) {
         if(moveNode>=0){
            Node node = adjList.get(moveNode);
            node.xpos=e.getPoint().x;
            node.ypos=e.getPoint().y;
            repaint();
         }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
    	for(TopologyUI.Node node : adjList)
        {
            if(e.getPoint().x>node.xpos && e.getPoint().x<node.xpos+CIRCLEDIAMETER &&
                e.getPoint().y>node.ypos && e.getPoint().y<node.ypos+CIRCLEDIAMETER)
            {
                moveNode=node.nodeNum;
             }
        }

    }

    @Override
    public void mouseReleased(MouseEvent e) {
       moveNode = -1;
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    /**
     * 
     */
    private class Node   {

        public int xpos;
        public int ypos;
        public int[] edges; 
        public int nodeNum;
        public Point circleCenter;
        public ArrayList<Point> anchorPoints;
        public int inEdges, arraySpot;
        public Color color;
        public String nodeName;

        public Node()
        {
            xpos=0;
            ypos=0;
            nodeNum=0;
            circleCenter = new Point();
            anchorPoints=new ArrayList<Point>();
            inEdges=0;
            arraySpot=0;
            color=Color.BLACK;
            nodeName = "";
         }

        public void draw(Graphics g)
        {
            g.setColor(color);
            g.drawOval(xpos, ypos, CIRCLEDIAMETER, CIRCLEDIAMETER);
            g.setColor(Color.BLUE);
            g.drawString(nodeName, xpos+(CIRCLEDIAMETER/2), ypos+(CIRCLEDIAMETER/2)+5);
            g.setColor(Color.BLACK);
            circleCenter = new Point(xpos+(CIRCLEDIAMETER/2),ypos+(CIRCLEDIAMETER/2));
        }

    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
