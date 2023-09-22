package com.sywyar.openserver.jdialogs;

import com.sywyar.openserver.build.BuiltMain;
import com.sywyar.openserver.OpenServer;

import javax.swing.*;
import java.awt.*;
import java.io.File;

import static com.sywyar.openserver.OpenServer.image;

public class TipsJDialog extends JDialog {

    public TipsJDialog() {
        setIconImage(image);
        setTitle("Tips");
        setSize(800,400);
        setLocationRelativeTo(null);//窗口居中
        setResizable(false);//窗口大小不可变
        setLayout(null);
        JCheckBox jCheckBox = new JCheckBox("不再提示");
        JButton jButton = new JButton("确定");
        JLabel jLabel = getTips();
        jButton.addActionListener(e -> {
            if (jCheckBox.isSelected()){
                File file = new File(System.getProperty("user.dir")+"//BuildTools//tips.tips");
                OpenServer.mkdirs(file);
                BuiltMain.Writer("true",file.getPath());
            }
            setVisible(false);
        });
        jLabel.setBounds(0,0,800,300);
        jButton.setBounds(700,320,100,50);
        jCheckBox.setBounds(0,320,700,50);
        add(jLabel);
        add(jCheckBox);
        add(jButton);
        setVisible(true);
    }

    private static JLabel getTips() {
        JLabel jLabel = new JLabel("<html><body>Tips<br>" +
                "1.一般来说，当您点击右上角的叉叉时，本软件会顺带强制结束掉构建和服务器进程<br>" +
                "2.构建过程非常慢，你可以在等待构建完成的时间内做其他的事情，甚至是睡一觉？<br>" +
                "3.第一次构建时(无论版本)会进行下载，如果太慢，您可以使用一些魔法来加速(cmd)<br>" +
                "4.无一例外的是，如果检测到了相对应版本的服务器核心，将不会进行重新构建<br>" +
                "5.当服务器核心不是最新的时候，需要重新构建，这时启动服务器核心会让您等待大几秒，大部分情况本软件能够捕捉到，只需在弹窗确定即可<br>" +
                "6.在某些没有捕捉到服务器核心过期的情况下，可以自行删除服务器核心(在本软件目录下的BuildTools中相对于的版本号文件夹下的spigot-版本号.jar文件就是您的核心)<br>" +
                "7.在删除对应版本的服务器核心后重新启动本软件并选择相应版本就可以重新构建<br>" +
                "8.感谢您使用本软件，遇到问题发送邮件(Sywyari@163.com)即可" +
                "</body></html>");
        Font font = new Font("微软雅黑", Font.BOLD, 13);
        jLabel.setFont(font);
        jLabel.setHorizontalAlignment(SwingConstants.LEFT);
        jLabel.setVerticalAlignment(SwingConstants.TOP);
        return jLabel;
    }
}
