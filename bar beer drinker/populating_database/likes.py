import csv
import random

def getLikes():
    count = 0
    with open('likes.csv', 'w') as likescsv:
        likes = csv.writer(likescsv, escapechar='%', lineterminator='\n', quoting=csv.QUOTE_NONE)
        with open('beers_ready.csv', 'r') as beerscsv:
            beers = csv.reader(beerscsv)
            for row_beers in beers:
                with open('drinkers_ready.csv', 'r') as drinkerscsv:
                    drinkers = csv.reader(drinkerscsv)
                    for row_drinkers in  drinkers:
                        if count >= 1:
                            count = 0
                            break
                        elif random.randint(0,2) == 0:
                            row_likes = list()
                            row_likes.extend((row_drinkers[0], row_beers[0]))
                            likes.writerow(row_likes)
                            count += 1

getLikes()