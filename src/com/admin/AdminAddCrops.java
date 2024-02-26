package com.admin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.connection.DatabaseConnection;


/**
 * Servlet implementation class AdminAddCrops
 */
@WebServlet("/AdminAddCrops")
@MultipartConfig
public class AdminAddCrops extends HttpServlet {

	protected void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException{
		  response.setContentType("image/jpeg");
		  int id = Integer.parseInt(request.getParameter("id"));
		  Connection conn = DatabaseConnection.getConnection();
		  String sql = "SELECT * FROM tblcrops WHERE ID ='"+id+"'";
		  PreparedStatement ps;
		  try {
		   ps = conn.prepareStatement(sql);
		   ResultSet rs = ps.executeQuery();
		   if(rs.next()){
		    byte [] imageData = rs.getBytes("crop_image"); // extract byte data from the resultset..
		    OutputStream os = response.getOutputStream(); // output with the help of outputStream 
		             os.write(imageData);
		             os.flush();
		             os.close();
		   }
		  } catch (SQLException e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
		   response.getOutputStream().flush();
		   response.getOutputStream().close();
		  }
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		int Cid = Integer.parseInt(request.getParameter("cropId"));
		String cname = request.getParameter("cname");
		String month = request.getParameter("month");
		String description = request.getParameter("description");
		Part part = request.getPart("file");
		if (part != null) {
			System.out.println(part.getName());
			System.out.println(part.getContentType());
			System.out.println(part.getSize());
			System.out.println(Cid);
		}
		InputStream img = part.getInputStream();
		Connection connection = DatabaseConnection.getConnection();
		try {
			PreparedStatement ps = connection.prepareStatement(
					"insert into tblcrops(id,crop_name,description,month,crop_image)value(?,?,?,?,?)");
			ps.setInt(1, Cid);
			ps.setString(2, cname);
			ps.setString(3, month);
			ps.setString(4, description);
			ps.setBlob(5, img);
			int i = ps.executeUpdate();
			if (i > 0) {
				String success = "Crop added successfully.";
				session.setAttribute("message", success);
				response.sendRedirect("admin-manage-crops.jsp");
			} else {
				String error = "Crop added failed.";
				session.setAttribute("message", error);
				response.sendRedirect("admin-manage-crops.jsp");
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
