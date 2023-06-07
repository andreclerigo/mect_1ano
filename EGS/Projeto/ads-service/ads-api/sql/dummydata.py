from pymongo import MongoClient

client = MongoClient('localhost', 27017)

db = client['ads-api']
ads = db.ads
ads.insert_one({  'id': 12, \
                    'type':"image", \
                    'description':"Continente Modelo", \
                    'pricing_model':"CPC", \
                    'age_range':"adults", \
                    'location':"portugal", \
                    "ad_creative": "https://mc.sonae.pt/wp-content/uploads/2020/12/Continente-Modelo-Vila-Real-Santo-Antonio_12.jpg", \
                    "impressions": 11, \
                    "clicks": 4, \
                    "user": 5, \
                    "target": 100, \
                    "active": 1, \
                    "destination": "https://www.continente.pt/", \
                    })