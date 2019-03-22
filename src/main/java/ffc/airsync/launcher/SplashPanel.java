/*
 * Copyright 2019 NSTDA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 */
package ffc.airsync.launcher;

import javax.swing.*;
import java.awt.*;

/**
 * @author piruin
 */
public class SplashPanel extends javax.swing.JPanel {

    private static Image backgroud;
    boolean painted = false;

    /**
     * Creates new form SplashPanel
     */
    public SplashPanel() {
        backgroud = new ImageIcon(getClass().getClassLoader().getResource("bg/720p.jpg")).getImage();
        initComponents();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!painted) {
            g.drawImage(backgroud, 0, 0, null);
            painted = true;
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

        progressBar = new javax.swing.JProgressBar();
        versionText = new javax.swing.JLabel();

        setBackground(new java.awt.Color(76, 189, 148));
        setPreferredSize(new java.awt.Dimension(1440, 900));

        progressBar.setBackground(new Color(0,0,0,0));
        progressBar.setForeground(new java.awt.Color(255, 255, 255));
        progressBar.setBorderPainted(false);
        progressBar.setDoubleBuffered(true);
        progressBar.setPreferredSize(new java.awt.Dimension(146, 8));
        progressBar.setRequestFocusEnabled(false);

        versionText.setBackground(new java.awt.Color(0, 0, 0));
        versionText.setForeground(new java.awt.Color(255, 255, 255));
        versionText.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        versionText.setText("jLabel1");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(327, 327, 327)
                        .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 1054, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(58, 58, 58)
                        .addComponent(versionText)))
                .addContainerGap(59, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(versionText)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 784, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(72, 72, 72))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JProgressBar progressBar;
    protected javax.swing.JLabel versionText;
    // End of variables declaration//GEN-END:variables
}
