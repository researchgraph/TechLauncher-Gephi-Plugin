package org.gephi.leapmotion.controller;

import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.project.api.WorkspaceListener;
import org.gephi.layout.api.LayoutModel;
import org.gephi.layout.api.LayoutController;
import org.gephi.layout.spi.Layout;
import org.gephi.layout.spi.LayoutBuilder;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import org.gephi.layout.plugin.rotate.*;
import com.leapmotion.leap.*;

/**
 * Command to install Leap Motion SDK to repo:
 mvn org.apache.maven.plugins:maven-install-plugin:2.3.1:install-file \
 -Dfile=PATH_TO_JAR \
 -DgroupId=com.leapmotion.leap -DartifactId=leapMotion \
 -Dversion=1.0.0 -Dpackaging=jar
 *
 * @author Hao Zhang
 */
@ServiceProvider(service = WorkspaceListener.class)
public class LeapMotionController implements WorkspaceListener {

    @Override
    public void initialize(Workspace wrkspc) {
    }

    @Override
    public void select(Workspace wrkspc) {
        // connect to Leap Motion
        Controller controller = new Controller();
        LMListener listener = new LMListener();
        controller.addListener(listener);

        // String name = wrkspc.getLookup().lookup(WorkspaceInformation.class).getName();
        // System.out.println("Workspace '" + name + "' selected");
    }

    @Override
    public void unselect(Workspace wrkspc) {
    }

    @Override
    public void close(Workspace wrkspc) {
    }

    @Override
    public void disable() {
    }
}

class LMListener extends Listener {
    ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
    Workspace wrkspc = pc.getCurrentWorkspace();
    LayoutController lc;
    LayoutModel lm = wrkspc.getLookup().lookup(LayoutModel.class);
    Layout layout;
    int mark = 1, count = 0;

    public void onInit(Controller controller){
        System.out.println("Leap Motion Initialized");
    }

    public void onConnect(Controller controller){
        System.out.println("Leap Motion Connected");
    }

    public void onDisconnect(Controller controller){
        System.out.println("Leap Motion Disconnected");
    }

    public void onExit(Controller controller){
        System.out.println("Leap Motion Exited");
    }

    public void onFrame(Controller controller){
        Frame frame = controller.frame(0);

        for(Hand hand: frame.hands()){
            String handType = hand.isLeft()?"Left hand": "Right hand";

            // Sphere Detection
            //System.out.println(hand.sphereRadius());
            if (hand.sphereRadius() < 50) {
                layout = lm.getSelectedLayout();

                if (layout != null && !lm.isRunning()) {
                    lc = Lookup.getDefault().lookup(LayoutController.class);
                    lc.setLayout(layout);
                    if (lc.canExecute()) {
                        lc.executeLayout();
                        System.out.println("-- Layout is executed");
                    }
                }
            } else if (hand.sphereRadius() > 150){
                layout = lm.getSelectedLayout();

                if (lm.isRunning()) {
                    lc = Lookup.getDefault().lookup(LayoutController.class);
                    System.out.println("-- Layout is running");
                    if (lc.canStop()) {
                        lc.stopLayout();
                        System.out.println("-- Layout stopped");
                    }
                }
            }

            // Flip Detection
            Vector normal = hand.palmNormal();
            float status = normal.getY();

            if (status * mark > 0) {
                mark *= -1;
                count++;
            }
            if (count == 2) {
                System.out.println("Flip Detected");
                count = 0;
                Layout layout = new Rotate().buildLayout();
                lc = Lookup.getDefault().lookup(LayoutController.class);
                lc.setLayout(layout);
                if (lc.canExecute()) {
                    lc.executeLayout();
                    System.out.println("-- Layout is executed");
                }
            }
        }

        GestureList gestures = frame.gestures();
        if (gestures.count() > 0) System.out.println("Gesture Detected");
    }
}