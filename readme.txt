  ______      __        _    __         __       ___
 /_  __/___ _/ /_____ _(_)  / /_  ___  / /_____ <  /
  / / / __ `/ __/ __ `/ /  / __ \/ _ \/ __/ __ `/ / 
 / / / /_/ / /_/ /_/ / /  / /_/ /  __/ /_/ /_/ / /  
/_/  \__,_/\__/\__,_/_/  /_.___/\___/\__/\__,_/_/   
                                                    
By Edward Zhang (ezha210) & Zach Huxford (zhux228)

Requirements:
	> Java 1.8_60 or later
	> JavaFX Runtime
	> HTK (HVite) and Catherine's Training Files
	> Audio Input and Output devices
	> Screen Resolution >= 1024x768

How to run:
	Place the .jar file in the same directory as Catherine's HTK training files (the HTK folder) and make sure HVite is in your PATH.

	Example folder structure

	documents/
	|-- tatai.jar
	|-- HTK/
	    |-- MaoriNumbers/
	        |-- HMMs/
	        |   L-- etc.
	        |-- user/
	        |   L-- etc.
	        L-- HVite.exe (Optional: Only if you're running on Windows)

	Then, run java -jar tatai.jar from the same directory as the jar (documents in this case)
	You should then see the GUI appear. If it doesn't see FAQs below.

	Multiuser support is implemented in this release however if you do not wish to use a different user just press "Login" as the precreated default user

FAQs/Troubleshooting:

> The GUI doesn't appear, and I see "Cannot find class tatai.app.Main" in the terminal
	JavaFX is probably not installed correctly on your system. This is usually the case if you are using OpenJDK/OpenJRE. Please try executing "sudo apt install openjfx" and try again.

> The audio recording doesn't work / I can't hear anything when I press play
	Tatai automatically uses your default audio recording / playback device. Please check your system settings and try again

> Tatai's Animations are playing slowly
	While ideally you would just get a better computer or stop using a Virtual Machine, we understand this is sometimes unavoidable. In these cases, please enter "Settings" then "General" and enable "Disable Animations"

> I get the error "HTK Machine Broke: Catherine HTK training Files missing" when opening Tatai
	Make sure the HTK folder is in the correct place.

> What is "tataiData.sqlite"?
	This is the database that stores Tatai's save data. Do not delete it unless you want to clear your savedata.