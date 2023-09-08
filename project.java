package com.demo.ExcelProject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

class project {

	public static void main(String args[]) {

		Scanner sc = new Scanner(System.in);
		int s = sc.nextInt();
		int e = sc.nextInt();
		String day = sc.next();
		String module = sc.next();

		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/login", "root", "");
			Statement stmt = con.createStatement();
			ResultSet rs1 = stmt.executeQuery("SELECT FacultyName FROM `excel1` where Module='" + module + "'");

			Statement stmt1 = con.createStatement();
			ResultSet rs2 = stmt1.executeQuery("select * from `excel1` where `Day of the Week`='" + day + "'");
			HashSet<String> facLis = new HashSet<>();
			HashSet<String> facLis1 = new HashSet<>();
			while (rs2.next()) {
				String s1 = rs2.getString(6).split(":")[0];
				String e1 = rs2.getString(7).split(":")[0];

				if (!isTimeOverLapping(s, e, Integer.parseInt(s1), Integer.parseInt(e1))) {
					facLis1.add(rs2.getString("FacultyName"));
					if (rs2.getString(10).equals(module)) {
						facLis.add(rs2.getString("FacultyName"));
					}
				}
			}

			boolean isFound = false;
			if (facLis.size() != 0) {
				isFound = true;
				System.out.println("Message:substitute avaliable for the respective module");
			} else if (facLis1.size() != 0) {
				System.out.println("Message: no substitute avaliable for the respective module");
			} else {
				System.out.println("Message:no substitute avaliable");
			}

			HashMap<String, Integer> map = new HashMap<>();
			if (isFound) {
				int index = 0;
				for (String str : facLis) {
					map.put(str, index++);
				}
			} else {
				int index = 0;
				for (String str : facLis1) {
					map.put(str, index++);
				}
			}
			int load[] = new int[map.size()];
			for (String key : map.keySet()) {
				Statement stmt2 = con.createStatement();
				ResultSet rs3 = stmt1.executeQuery("select * from `excel1` where (`Day of the Week`='" + day
						+ "' AND `FacultyName`='" + key + "')");
				while (rs3.next()) {
					int start = Integer.parseInt(rs3.getString(6).split(":")[0]);
					int end = Integer.parseInt(rs3.getString(7).split(":")[0]);
					if (end < start)
						end = 12 + end;
					load[map.get(key)] = load[map.get(key)] + (end - start);
				}
			}

			String lis[] = new String[map.size()];
			int idx = 0;
			for (String key : map.keySet())
				lis[idx++] = key;

			for (int i = 0; i < lis.length - 1; i++) {
				for (int j = 0; j < lis.length - i - 1; j++) {
					if (load[j] > load[j + 1]) {
						int temp = load[j];
						load[j] = load[j + 1];
						load[j + 1] = temp;

						String str = lis[j];
						lis[j] = lis[j + 1];
						lis[j + 1] = str;
					}
				}
			}

			print(lis, load);
			con.close();
		} catch (Exception ex) {
			System.out.println(ex);
		}
	}

	public static void print(String[] lis, int[] load) {
		for (int i = 0; i < lis.length; i++) {
			if (lis[i].length() < 30) {
				while (lis[i].length() < 30) {
					lis[i] += " ";
				}
			}
			System.out.println("name:" + lis[i] + "      " + "load[" + load[i] + "]");
		}
	}

	public static boolean isTimeOverLapping(int s, int e, int s1, int e1) {
		if (s1 >= s && s1 <= e)
			return true;
		else if (e1 >= s && e1 <= e)
			return true;
		else
			return false;
	}
}
