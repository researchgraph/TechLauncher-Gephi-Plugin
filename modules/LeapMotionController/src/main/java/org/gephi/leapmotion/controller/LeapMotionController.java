package org.gephi.leapmotion.controller;

import org.gephi.project.api.Workspace;
import org.gephi.project.api.WorkspaceInformation;
import org.gephi.project.api.WorkspaceListener;
import org.gephi.layout.api.LayoutModel;
import org.gephi.layout.api.LayoutController;
import org.gephi.layout.spi.Layout;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
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
    LayoutController lc;
    LayoutModel lm;
    Layout layout;

    @Override
    public void initialize(Workspace wrkspc) {
    }

    @Override
    public void select(Workspace wrkspc) {
        Controller controller = new Controller();

        String name = wrkspc.getLookup().lookup(WorkspaceInformation.class).getName();

        System.out.println("Workspace '" + name + "' selected");

        lm = wrkspc.getLookup().lookup(LayoutModel.class);
        layout = lm.getSelectedLayout();

        if (lm.isRunning()) {
            lc = Lookup.getDefault().lookup(LayoutController.class);
            System.out.println("Layout is running");
            if (lc.canStop()) {
                lc.stopLayout();
                System.out.println("Layout stopped");
            }
        } else {
            if (layout != null && !lm.isRunning()) {
                lc = Lookup.getDefault().lookup(LayoutController.class);
                lc.setLayout(layout);
                if (lc.canExecute()) {
                    lc.executeLayout();
                }
            }
        }

        System.out.println("Is layout running? " + lm.isRunning());
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
