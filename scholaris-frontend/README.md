# Scholaris: Next-Gen Scholar Matching System

## What is Scholaris?
Scholaris is a Java-based desktop application designed to streamline the scholarship discovery process. Instead of manually searching through massive databases of financial aid, Scholaris acts as an intelligent matching engine that pairs you with opportunities tailored to your specific profile.

## Purpose
The purpose of the app is to save students hours of time and frustration by automatically filtering out scholarships they don't qualify for. By analyzing a student's minimum GPA, age limit, and field of study, Scholaris guarantees that the opportunities shown are highly relevant to their academic journey.

## How to Run the App
You do **not** need to install Java, Maven, or any other programming tools to run this app. It has been bundled as a standalone Windows executable.

1. Open your File Explorer.
2. Navigate to the following folder inside the project:
   `scholaris-frontend\target\installer\Scholaris`
3. Find the file named **`Scholaris.exe`**.
4. Double-click **`Scholaris.exe`** to launch the application. 

*(Note: If you want to share the app with friends, simply right-click the `Scholaris` folder inside `target\installer`, compress it to a ZIP file, and send it to them. They can extract it and run the `.exe` directly!)*

## How to Use the App
1. **Welcome Screen:** Once the app launches, click the **"Find My Scholarship →"** button on the landing page.
2. **Create Your Profile:** Fill in your academic details:
   * **Age:** Enter your current age (e.g., 21).
   * **GPA:** Enter your GPA out of 4.0 (e.g., 3.85).
   * **Nationality:** Select your country from the dropdown menu.
   * **Field of Study:** Select your major/category from the dropdown list.
3. **Match Me:** Click the **"Match Me →"** button. The intelligent engine will instantly filter the dataset.
4. **View Matches:** You will see a scrollable grid of scholarship cards that you are perfectly eligible for.
5. **Get Details:** Click **"View Details →"** on any scholarship card to read a comprehensive overview and find the button to visit the official scholarship website. You can also use the top navigation bar to page through your matches or return to your profile to adjust your details.
