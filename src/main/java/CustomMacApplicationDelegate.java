import com.sun.glass.ui.Application;
import com.sun.glass.ui.mac.MacApplication;
import com.sun.glass.ui.mac.MacApplicationDelegate;

public class CustomMacApplicationDelegate extends MacApplicationDelegate {

    @Override
    public boolean applicationSupportsSecureRestorableState() {
        return true;
    }

    public static void setDelegate() {
        MacApplication macApp = (MacApplication) Application.GetApplication();
        macApp.setDelegate(new CustomMacApplicationDelegate());
    }
}