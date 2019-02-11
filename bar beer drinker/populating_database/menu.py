import csv
import random

def getMenu():
    count = 0
    with open('bars_ready.csv', 'r') as csvfile:
        reader_bars = csv.reader(csvfile)
        with open('menuItems.csv', 'r') as csvfile2:
            reader_items = csv.reader(csvfile2)
            with open('temp2.csv', 'w') as  csvfile3:
                writer = csv.writer(csvfile3, escapechar='%', lineterminator='\n', quoting=csv.QUOTE_NONE)
                for row_bar in reader_bars:
                    for row_item in reader_items:
                        row = list()
                        if count == 4:
                            count = 0
                            break
                        else:
                            count += 1
                            row.append(row_bar[0])
                            row.append(row_item[0])
                            row.append(str(random.randint(1,30)))
                        writer.writerow(row)
                        print(row)
getMenu()