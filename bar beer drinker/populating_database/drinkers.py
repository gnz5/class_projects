import csv
import random

def getDrinkers():
    with open('street_addresses.csv', 'r') as csvfile:
        reader = csv.reader(csvfile)
        count = 0
        with open('temp.csv', 'w') as  drinkers:
            writer = csv.writer(drinkers, escapechar='%', lineterminator='\n', quoting=csv.QUOTE_NONE)
            for row in reader:
                if count % 10 == 0:
                    phone = ''
                    for i in range(10):
                        phone += str(random.randint(0, 9))
                    row.append(phone)
                    writer.writerow(row)
                    print(row)
                count += 1
getDrinkers()