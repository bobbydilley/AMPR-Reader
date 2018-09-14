# ANPR-Reader

This is an automatic number plate recognition project that I created to test my own algorithm. There are directorys for version 1 and version 2 of the software. Version 2 is a rewritten version which much neater code, however version 1 seems to have a much better success rate on reading plates.

# The Project

For this project I set out to read UK car number plates using my own algorithm in java. The project worked semi-successfully being able to read individual letters with quite good accuracy. In the future I would need to add the ability to read other letters (which would be trivial) as well as locating the number plate in a picture (which I beleive would be more difficult)

# The Algorithm

The algorithm is very simple:

## To learn a digit/letter

1. From a full picture of a car number plate, simply move from left to right scanning all pixels vertically downwards. Record where you start to see black pixels, and where you stop seeing them. This will give you the width of the first digit/letter. Do this from top to bottom as well, this time scanning all pixels horizontally, and you will find the height. This allows us to have an image with just the first character.

2. Draw a 6x6 (different grid sizes may work better) over the image, and for each grid block write down the percentage of black to yellow pixels. And save these 36 values along with the corresponding letter in a file.

3. Repeat this for all letters until you have a file with all of the percentages in.

## To read a digit/letter

1. Same as step 1 for learning

2. Do the same as step 2, however this time once you've written down the 36 values check them against the file with the recorded values for other letters. The letter in the file with the values most similar to the current digit/letter you are reading would be the best guess at the letter.


