	package practiceselenium;
	
	import org.openqa.selenium.By;
	import org.openqa.selenium.WebDriver;
	import org.openqa.selenium.WebElement;
	import org.openqa.selenium.chrome.ChromeDriver;
	import org.testng.Assert;
	import org.testng.annotations.*;
	
	import java.util.*;
	
	import java.time.Duration;
	
	public class dreamportal1 {
	
	    WebDriver driver;
	    Set<String> recurringDreamsFromDiary = new HashSet<>();
	
	    
	    @BeforeClass
	    public void setup() throws Exception {
	        System.setProperty("webdriver.chrome.driver", "D:\\softwares\\chromedriver-win64\\chromedriver.exe");
	        driver = new ChromeDriver();
	
	               driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
	
	        driver.manage().window().maximize();
	    }
	
	
	    @Test(priority = 1)
	    public void testHomePage() throws InterruptedException {
	        driver.get("https://arjitnigam.github.io/myDreams/");
	
	        
	        WebElement loadingAnimation = driver.findElement(By.id("loadingAnimation"));
	        Assert.assertTrue(loadingAnimation.isDisplayed(), "Loading animation not visible initially");
	
	        
	        Thread.sleep(4000);
	
	        		
	        Assert.assertTrue(loadingAnimation.getAttribute("class").contains("hidden"), "Loading animation did not disappear");
	
	        WebElement mainContent = driver.findElement(By.id("mainContent"));
	        Assert.assertTrue(mainContent.isDisplayed(), "Main content is not visible");
	
	        
	        WebElement dreamButton = driver.findElement(By.id("dreamButton"));
	        Assert.assertTrue(dreamButton.isDisplayed(), "Dream button not visible");
	
	        dreamButton.click();
	
	        
	        String originalHandle = driver.getWindowHandle();
	        for (String handle : driver.getWindowHandles()) {
	            if (!handle.equals(originalHandle)) {
	                driver.switchTo().window(handle);
	                if (driver.getCurrentUrl().contains("dreams-diary.html")) {
	                    testDreamsDiaryPage();
	                } else if (driver.getCurrentUrl().contains("dreams-total.html")) {
	                    testDreamsTotalPage();
	                }
	                driver.close(); // close this tab after test
	            }
	        }
	
	        // Switch back to original
	        driver.switchTo().window(originalHandle);
	    }
	
	    public void testDreamsDiaryPage() {
	        // Validate there are exactly 10 dream entries
	        List<WebElement> rows = driver.findElements(By.cssSelector("table tbody tr"));
	        Assert.assertEquals(rows.size(), 10, "Dream entries count mismatch!");
	
	        
	        Map<String, Integer> dreamCount = new HashMap<>();
	
	       
	        for (WebElement row : rows) {
	            List<WebElement> cols = row.findElements(By.tagName("td"));
	            Assert.assertEquals(cols.size(), 3, "Row does not have 3 columns");
	
	            for (WebElement col : cols) {
	                Assert.assertFalse(col.getText().trim().isEmpty(), "A column is empty in diary table");
	            }
	
	            
	            String type = cols.get(2).getText();
	            Assert.assertTrue(type.equals("Good") || type.equals("Bad"), "Dream type is not Good/Bad");
	
	            String dreamName = cols.get(0).getText().trim();
	            dreamCount.put(dreamName, dreamCount.getOrDefault(dreamName, 0) + 1);
	        }
	
	       
	        for (Map.Entry<String, Integer> entry : dreamCount.entrySet()) {
	            if (entry.getValue() > 1) {
	                recurringDreamsFromDiary.add(entry.getKey());
	            }
	        }
	    }
	
	    public void testDreamsTotalPage() {
	        
	        String goodDreams = driver.findElement(By.id("goodDreams")).getText();
	        String badDreams = driver.findElement(By.id("badDreams")).getText();
	        String totalDreams = driver.findElement(By.id("totalDreams")).getText();
	        String recurringDreams = driver.findElement(By.id("recurringDreams")).getText();
	
	        Assert.assertEquals(goodDreams, "6");
	        Assert.assertEquals(badDreams, "4");
	        Assert.assertEquals(totalDreams, "10");
	        Assert.assertEquals(recurringDreams, "2");
	
	                WebElement recurringList = driver.findElement(By.id("recurringList"));
	        String recurringText = recurringList.getText();
	
	        for (String dream : recurringDreamsFromDiary) {
	            Assert.assertTrue(recurringText.contains(dream), "Recurring dream missing in totals: " + dream);
	        }
	
	
	        Assert.assertTrue(recurringText.contains("Flying over mountains"));
	        Assert.assertTrue(recurringText.contains("Lost in maze"));
	    }
	
	    @AfterClass
	    public void tearDown() {
	        if (driver != null) {
	            driver.quit();
	        }
	    }
	}
