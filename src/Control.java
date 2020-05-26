import static org.junit.Assert.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class Control {
	@Test
	public final void test() throws IOException, InterruptedException {
		System.setProperty("webdriver.chrome.driver", "chromedriver");
		// Creating chrome options instance:
		ChromeOptions options = new ChromeOptions();
		options.addArguments("user-data-dir=profile");
		// Initializing Chrome browser driver:
		WebDriver driver = new ChromeDriver(options);
		// increasing the dimension so next page can be clicked

		driver.manage().window().setPosition(new Point(0, 0));
		driver.manage().window().setSize(new Dimension(1440, 768));
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);
		driver.manage().timeouts().setScriptTimeout(5, TimeUnit.SECONDS);
		BufferedReader br = new BufferedReader(new FileReader("EE_DATA_COLLECTION.csv"));
		String line = br.readLine();
		ArrayList<String> profileURLs = new ArrayList<String>();
		profileURLs.add(line);
		while ((line = br.readLine()) != null)
			profileURLs.add(line);
		BufferedWriter bw = new BufferedWriter(new FileWriter("count_control.txt"));
		for (int i = 0; i < profileURLs.size(); i++) {
			driver.get(profileURLs.get(i));
			Thread.sleep(200);
			// The script will not collect any data but it will verify whether the page is
			// loaded andthe information is found.
			try {
				// Verify information is found
				driver.findElement(By.xpath("//h1[starts-with(@class,'pv-top-card-section__name')]"));

				String p = "\n" + (i + 1) + "";
				System.out.println(p + " passed");
				bw.write(p);
				bw.flush();
			} catch (NoSuchElementException e) {
				// It will be stopped when the information cannot be foundanymore i.e. when the
				// bot is stopped.
				System.out.println("broken");
				break;
			}
		}
	}
}