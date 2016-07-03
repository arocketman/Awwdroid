Awwdroid is an android app that allows you to view images from Reddit's [/r/aww](http://reddit.com/r/aww) .

### The presentation

You can find the app at the google play store: [Link](https://play.google.com/store/apps/details?id=it.arocketman.awwdroid)

### Application structure: some UML

Here's a description of the core classes and their responsabilities:

*   **MainActivity** : Is the only activity of the app. It has a dynamic ViewPager that instantiates fragments.
*   **SingleImageFragment** : Fragment that is referenced to a single image and handles that image visualization.
*   **FetchJSONTask**: AsyncTask that calls methods on the RedditFetcher class.
*   **RedditFetcher** : Handles the calls to Reddit's API.
*   **ImageEntry** : Single Image representation.

The application is structured as follows:   [![](http://i.imgur.com/T8t1PG2.png)](http://i.imgur.com/T8t1PG2.png) The following communication diagram explains how the communication between objects work: [![](http://i.imgur.com/Ptnb736.png)](http://i.imgur.com/Ptnb736.png) The following sequence diagram explains how the RedditFetcher creates an ArrayList of ImageEntry objects calling the Reddit APIs: 

[![](http://i.imgur.com/y3fxtzP.png)](http://i.imgur.com/y3fxtzP.png)
