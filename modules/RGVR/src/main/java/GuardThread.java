/**
 * @author Hao Zhang
 * This plugin is now able to monitor the click on workspaces
 * Run instructions:
 * mvn package
 * mvn org.gephi:gephi-maven-plugin:run
 */

import org.gephi.project.api.Workspace;
import org.gephi.project.api.WorkspaceInformation;
import org.gephi.project.api.WorkspaceListener;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = WorkspaceListener.class)
public class GuardThread implements WorkspaceListener {

    @Override
    public void initialize(Workspace wrkspc) {
    }

    @Override
    public void select(Workspace wrkspc) {

        String name = wrkspc.getLookup().lookup(WorkspaceInformation.class).getName();
        System.out.println("Workspace '" + name + "' selected");

//        LeapMotionController monitor = new LeapMotionController();
        /*
         * Plan is to append the above listener to workspaces
         * to make Gephi GUI react to certain activities
         */
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
