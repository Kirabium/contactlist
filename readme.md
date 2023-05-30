## 🤖 ContactList - Android Technical Test
# Description
ContactList is a simple Android application developed as part of a technical test. The objective was to create an offline contact management application using Kotlin, which can fetch data from a network API, display a list of contacts and their details. This application supports two views, XML and Jetpack Compose.

# Instructions
The application was built following these instructions:

- Fetch data from randomuser.me API using the latest version of the API (1.3)
- Display a paginated list of contacts with infinite scroll
- Display details of a contact when clicked from the list
- Provide an offline mode and handle connectivity issues during app usage
- All chosen dependencies can be justified and no non-official or unstable libraries were used
  
# Technologies Used
- Kotlin: The entire project is written in Kotlin
- Room: Used for local data storage
- Retrofit: Used for network requests
- Material: Used for the app's UI components
- Gson: Used for parsing JSON data
- Hilt: Used for dependency injection
- ViewModel: Used for managing UI related data in a lifecycle conscious way
- Coroutines: Used for managing background threads with simplified code and reducing needs for callbacks
  
# Features
Checks for local data existence:
- If available, display data from Room
- If not available, make a network request and then store the data in Room for future use
  
- Delete a contact by swiping left on the item (the contact is deleted only from Room as remote deletion is not possible)
- Refresh data by pulling the list down, this action clears Room and resynchronizes the first page's data with the remote data (deleted contacts reappear)
 
# Architecture and Testing
The architecture is based on classic MVVM with UseCases, which have been tested (a bit) through unit tests.

# Installation

Clone this repository and import into Android Studio
git clone https://github.com/Kirabium/contactlist.git
You need to use Java SDK 17 to be able to compile the project

# Links
XML : https://github.com/Kirabium/contactlist

Compose : https://github.com/Kirabium/contactlist/tree/compose

# Notes
Due to time constraints, some user experience improvements and the Compose version's features were not fully developed.

# Contact
For any additional questions or feedback, please feel free to reach out.

Thanks again for your time and consideration!
