import static org.junit.Assert.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class IPRotation {
	HashMap<String, String> proxyServers = new HashMap<String, String>();
	ArrayList<String> ipList = new ArrayList<String>();

	@Test
	public final void test() throws InterruptedException, IOException {
		// Sort the proxies into a list of IP addresses and ports.
		BufferedReader br = new BufferedReader(new FileReader("ipAddresses.csv"));
		String line = br.readLine();
		String key = line.split(",")[0];
		String value = line.split(",")[1];
		proxyServers.put(key, value);

		while ((line = br.readLine()) != null) {
			key = line.split(",")[0];
			value = line.split(",")[1];
			ipList.add(key);
			proxyServers.put(key, value);
		}
		br.close();
		System.setProperty("webdriver.chrome.driver", "chromedriver");
		br = new BufferedReader(new FileReader("EE_DATA_COLLECTION.csv"));
		line = br.readLine();
		ArrayList<String> profileURLs = new ArrayList<String>();
		profileURLs.add(line);
		while ((line = br.readLine()) != null)
			profileURLs.add(line);
		Random gen = new Random();
		BufferedWriter bw = new BufferedWriter(new FileWriter("count_iprotation.txt"));
		WebDriver driver = null;
		int beg = 0;
		for (int i = beg; i < profileURLs.size(); i++) {
			if (i == beg && driver == null) {

				try {
					// This is setting the timeout for slower IP so that it can automatically change
					// to another IP if that one timeout.
					System.out.println("1st: run at i:" + i);
					driver = ipRotation();
					driver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
					driver.manage().timeouts().setScriptTimeout(20, TimeUnit.SECONDS);
				} catch (Exception e) {
					System.out.println("5th: run at i:" + i);
					driver.close();
					i--;
					continue;
				}
			}
			// This if statement is to rotate the IP at every 10 profiles hit
			if (i != beg && i % 10 == 0) {
				System.out.println("2nd: run at i:" + i);
				driver.close();
				driver = ipRotation();
				driver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);

				driver.manage().timeouts().setScriptTimeout(20, TimeUnit.SECONDS);
			}
			try {
				System.out.println("3rd: run at i:" + i);
				driver.get(profileURLs.get(i));
				Thread.sleep(1000);
				try {
					driver.findElement(By.xpath("//div[@id='main-frame-error']"));
					throw new Exception();
				} catch (NoSuchElementException e) {
					try {
						driver.findElement(By.xpath("//h1[starts-with(@class,'pv-top-card-section__name')]"));
						String p = "\n" + (i + 1) + "";
						System.out.println(p + " passed");
						bw.write(p);
						bw.flush();
					} catch (NoSuchElementException ne) {
						System.out.println("broken at " + (i + 1));
						break;
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("4th: run at i:" + i);
				driver.close();
				driver = ipRotation();
				driver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
				driver.manage().timeouts().setScriptTimeout(20, TimeUnit.SECONDS);
				i--;
				continue;
			}
		}
	}

	/**
	 * This method creates a new webdriver with a different IP configuration that is
	 * chosen at random.
	 */
	public WebDriver ipRotation() {
		ChromeOptions options = new ChromeOptions();
		Random gen = new Random();
		options.addArguments("user-data-dir=profile");
		String ip = ipList.get(gen.nextInt(proxyServers.size() - 1));

		options.addArguments("--proxy-server=" + ip + ":" + proxyServers.get(ip));
		System.out.println("--proxy-server=" + ip + ":" + proxyServers.get(ip));
		return new ChromeDriver(options);
	}
}