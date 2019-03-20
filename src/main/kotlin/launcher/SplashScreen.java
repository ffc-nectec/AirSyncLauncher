package launcher;

import com.nstda.hii.airsynclauncher.BuildConfig;

import javax.swing.*;

public class SplashScreen {
    public JPanel panel;
    public JProgressBar progressBar;
    public JLabel progressText;
    public JLabel progressTitle;
    private JLabel versionLabel;

    public SplashScreen() {
        versionLabel.setText("v" + BuildConfig.VERSION);
    }
}
