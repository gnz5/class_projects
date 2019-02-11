import csv
import json
import requests
import random

# Generates names of bars along with their addresses using calls to the Google Maps API
def getBarData():
    ### load data from cities.json ###
    count = 0
    tax = {} # states is a dictionary whose keys are states and values are the states' tax rates
    open('bars.csv', 'w').close()
    with open('cities.json', 'r') as  json_data:
        cities = json.load(json_data)
    with open('states.csv', 'r') as states_file:
        for state in states_file:
            tax[state.replace('\n','')] = random.randint(1, 12)/100 # set tax rate to random float between 1 and 12 percent

    ### cycle through each city ###
    for i in range(0, len(cities)):
        current_city = cities[i]
        latitude = current_city["latitude"]
        longitude = current_city["longitude"]
        state = current_city["state"]

        ### set up URL for API call ###
        URL1 = f"https://maps.googleapis.com/maps/api/place/nearbysearch/json?location={latitude},{longitude}"
        URL2 = "&rankby=distance&type=bar&key=AIzaSyBVEkq_k7qYK_EsS72Gf_8J9oV6zrWfNPE"
        URL = URL1+URL2
        r = requests.get(url = URL)
        data = json.loads(r.text)
        data = data["results"]
        num_results = len(data)
        possible_opening_hours = [1000, 1100, 1200, 1300, 1400] # possible opening from 10am - 2pm
        possible_closing_hours = [2300, 2400,  100,  200,  300] # possible closing from 11pm - 3am

        ### get results and write results to csv ###
        with open('bars.csv', 'a') as  csvfile:
            writer = csv.writer(csvfile,lineterminator='\n', quoting=csv.QUOTE_NONE)
            for j in range(0, num_results):
                bar = data[j]
                opening = str(possible_opening_hours[random.randint(0, len(possible_opening_hours)-1)])
                closing = str(possible_closing_hours[random.randint(0, len(possible_opening_hours)-1)])
                address = bar["vicinity"].replace(",", "")
                license = state[0:2]
                for k in range(8):
                    license += str(random.randint(0, 9))
                phone = ''
                for k in range(10):
                    phone += str(random.randint(0, 9))
                row = list()
                row.extend((bar["name"].replace(',', ' '), license, phone, address, state, opening, closing, str(tax[state])))
                try:
                    writer.writerow(row)
                    count += 1
                    if count > 5000:
                        print("finished getting bars")
                        json_data.close()
                        csvfile.close()
                        return
                except UnicodeEncodeError:
                    print("UnicodeEncodeError when printing: " + str(row))

getBarData()