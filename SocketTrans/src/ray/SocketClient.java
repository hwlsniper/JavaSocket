package ray;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.io.RandomAccessFile;
import java.net.Socket;

public class SocketClient {
	//int UploadPercentage=0;
	private static boolean start = true;
	static LogService logService=new LogService();
	public static void main(String[] args) throws FileNotFoundException {
		String filename="D:\\socket_uploadfile\\bigfile.psd";
		File File2upload = new File(filename);
		uploadFile(File2upload,"localhost",6666);
		
	}
	/**
     * 上传文件
     *
     * @param uploadFile
     */
    public static void uploadFile(final File uploadFile,final String SeverAddress,final int port) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                	System.out.println("连接服务器 localhost 的 6666 端口...");
                	//int uploadsize=(int) uploadFile.length();
                    String souceid = logService.getBindId(uploadFile);
                    String head = "Content-Length=" + uploadFile.length() + ";filename=" + uploadFile.getName() + ";sourceid=" +
                            (souceid == null ? "" : souceid) + "\r\n";
                    
                    
                    Socket socket = new Socket(SeverAddress, port);
                    OutputStream outStream = socket.getOutputStream();
                    outStream.write(head.getBytes());
                    System.out.println("连接成功，发送请求连接头...");
                    PushbackInputStream inStream = new PushbackInputStream(socket.getInputStream());
                    String response = StreamTool.readLine(inStream);
                    String[] items = response.split(";");
                    String responseid = items[0].substring(items[0].indexOf("=") + 1);
                    String position = items[1].substring(items[1].indexOf("=") + 1);
                    if (souceid == null) {	
                    	//代表原来没有上传过此文件，往数据库添加一条绑定记录
                        logService.save(responseid, uploadFile);
                    }
                    System.out.println("开始传输文件："+uploadFile.getAbsolutePath());
                    
                    RandomAccessFile fileOutStream = new RandomAccessFile(uploadFile, "r");
                    fileOutStream.seek(Integer.valueOf(position));
                    byte[] buffer = new byte[1024];
                    int len = -1;
                    int length = Integer.valueOf(position);
                    while (start && (len = fileOutStream.read(buffer)) != -1) {
                        outStream.write(buffer, 0, len);
                        length += len;
                        //Message msg = new Message();
                        //msg.getData().putInt("size", length);
                        //handler.sendMessage(msg);
                    }
                    fileOutStream.close();
                    outStream.close();
                    inStream.close();
                    socket.close();
                    if (length == uploadFile.length()) logService.delete(uploadFile);
                    System.out.println("文件传输完毕！");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
