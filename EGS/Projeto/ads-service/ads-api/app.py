import random
from flask import Flask, jsonify, request, render_template_string, render_template, redirect
from uuid import uuid4
import hashlib
from flask_swagger_ui import get_swaggerui_blueprint
from flask_cors import CORS
import sqlite3
from pymongo import MongoClient

SWAGGER_URL = '/api/docs'  # URL for exposing Swagger UI (without trailing '/')
API_URL = '/static/swagger.json'  # Our API url (can of course be a local resource)

app = Flask(__name__)
CORS(app)

# Call factory function to create our blueprint
swaggerui_blueprint = get_swaggerui_blueprint(
    SWAGGER_URL,  # Swagger UI static files will be mapped to '{SWAGGER_URL}/dist/'
    API_URL,
    config={  # Swagger UI config overrides
        'app_name': "Advertisement service"
    },
)

app.register_blueprint(swaggerui_blueprint)
HOST = ''
PORT=8010

#Connect to the sqlite database
conn = sqlite3.connect('sql/database.db', check_same_thread=False)
cur = conn.cursor()

client = MongoClient('mongodb://mongodb:27017')
db = client['ads-api']
ADS_COLLECTION = db.ads
USERS_COLLECTION = db.users


age_groups = ["youth", "adults", "seniors"]

#function to get the token of an user
def get_token(user):
    token = hashlib.sha256(str(user['id']).encode('utf-8')).hexdigest() + hashlib.sha256(str(user['email']).encode('utf-8')).hexdigest() \
        + hashlib.sha256(str(user['type']).encode('utf-8')).hexdigest()
    return token

def get_token_from_request(id, email, type):
    token = hashlib.sha256(str(id).encode('utf-8')).hexdigest() + hashlib.sha256(str(email).encode('utf-8')).hexdigest() \
        + hashlib.sha256(str(type).encode('utf-8')).hexdigest()
    return token

#function to get the account type of an user from the token
def get_account_type(token):
    #get only the last 64 characters of the token
    token = token[-64:]
    if token == hashlib.sha256(str("A").encode('utf-8')).hexdigest():
        return "A"
    elif token == hashlib.sha256(str("C").encode('utf-8')).hexdigest():
        return "C"
    else:
        return "Internal error"
    
@app.route('/')
def index():
    global HOST
    HOST = request.host.split(":")[0] + ":"+str(PORT)
    return jsonify({'message': 'Welcome to the advertisement service', 'documentation': HOST + '/api/docs'})

@app.route('/v1/ads/<int:ad_id>', methods=['DELETE', 'GET']) # type: ignore
def update_ad(ad_id=None):
    global USERS_COLLECTION, ADS_COLLECTION
    if request.method == 'GET':
        #validate data
        if ad_id is None:
            return jsonify({'message': 'Missing required data'}), 400
        
        #ad = cur.execute("SELECT * FROM ads WHERE id = ?", (ad_id,)).fetchone()
        ad = ADS_COLLECTION.find_one({"id": ad_id})
        if ad is None:
            return jsonify({'message': 'Ad not found'}), 404
        if (ad['pricing_model'] == "CPC"):
            if(ad['clicks']+1 == ad['target']):
                # cur.execute("UPDATE ads SET active = ?, clicks = ? WHERE id = ?", (0, ad['clicks'] + 1, ad_id))
                ADS_COLLECTION.update_one({"id": ad_id}, {"$set": {"active": 0, "clicks": ad['clicks'] + 1}})
            else:
                # cur.execute("UPDATE ads SET clicks = ? WHERE id = ?", (ad['clicks'] + 1, ad_id))
                ADS_COLLECTION.update_one({"id": ad_id}, {"$set": {"clicks": ad['clicks'] + 1}})
            conn.commit()
            print("Ad clicked")
            return redirect(ad['destination'], code=302)
        else:
            return jsonify({'message': 'Ad not found'}), 404

    elif request.method == 'DELETE':
        print("DELETING AD")
        token = request.headers.get('Authorization')
        if token is None:
            return jsonify({'message': 'Missing token'}), 401
        if ad_id is None:
            return jsonify({'message': 'Missing ad id'}), 400

        token = token.split(' ')[1]
        #check if user is advertiser
        #users = cur.execute("SELECT * FROM users").fetchall()
        users = USERS_COLLECTION.find()
        for user in users:
            if get_token(user) == token:
                if user['type'] != "A":
                    return jsonify({'message': 'Consumers not authorized'}), 401
                #check if ad exists and if it belongs to the user
                #ads = cur.execute("SELECT * FROM ads WHERE user = ?", (user['id'],)).fetchall()
                ads = ADS_COLLECTION.find({"user": user['id']})
                for ad in ads:
                    print(str(ad['id']) + " " + str(ad_id))
                    if str(ad['id']) == str(ad_id):
                        #cur.execute("DELETE FROM ads WHERE id = ?", (ad_id,))
                        ADS_COLLECTION.delete_one({"id": ad_id})
                        #conn.commit()
                        return jsonify({'message': 'Ad deleted'}), 200
                    
                return jsonify({'message': 'Ad not found'}), 404
            
        return jsonify({'message': 'Invalid token'}), 401

#Endpoint to get ads (location and age_range are optional)
@app.route('/v1/ads', methods=['GET', 'POST'])
def get_ads():
    global USERS_COLLECTION, ADS_COLLECTION
    HOST = request.host.split(":")[0] + ":"+str(PORT)
    #check if the request is a post or get or delete
    if request.method == 'POST':
        data = request.get_json()
        token = request.headers.get('Authorization')
        if token is None:
            return jsonify({'message': 'Missing token'}), 401
        token = token.split(' ')[1]
        #check if user is advertiser
        #users = cur.execute("SELECT * FROM users").fetchall()
        users = USERS_COLLECTION.find()
        for user in users:
            if get_token(user) == token:
                if user['type'] != "A":
                    return jsonify({'message': 'Consumers not authorized'}), 401
                #validate data body
                if 'pricing_model' not in data or 'age_range' not in data or 'ad_creative' not in data or 'description' not in data or 'location' not in data or 'target' not in data or 'redirect' not in data:
                    print("age_range", data['age_range'])
                    print("pricing_model", data['pricing_model'])
                    print("ad_creative", data['ad_creative'])
                    print("description", data['description'])
                    print("location", data['location'])
                    print("target", data['target'])
                    print("redirect", data['redirect'])
                    return jsonify({'message': 'Missing required data'}), 400
                
                #create new ad and add it to the user's ads
                #new_id = cur.execute("SELECT MAX(id) FROM ads").fetchone()[0] + 1
                #check if there are ads in the database
                if ADS_COLLECTION.count_documents({}) == 0:
                    new_id = 1
                else:
                    new_id = ADS_COLLECTION.find_one(sort=[("id", -1)])['id'] + 1

                print(str(new_id) + " on user " + str(user['id']))
                # cur.execute("insert into ads (type, description, pricing_model, age_range, location, ad_creative, impressions, clicks, user, target, active, destination) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                #             , ("image", data['description'], data['pricing_model'], data['age_range'], data['location'], 
                #                data['ad_creative'], 0, 0, user['id'], data['target'], 1, data['redirect']))
                
                ads.insert_one({  'id': new_id, \
                    'type':"image", \
                    'description': data['description'], \
                    'pricing_model':data['pricing_model'], \
                    'age_range':data['age_range'], \
                    'location':data['location'], \
                    "ad_creative": data['ad_creative'], \
                    "impressions": 0, \
                    "clicks": 0, \
                    "user": user['id'], \
                    "target": data['target'], \
                    "active": 1, \
                    "destination": data['redirect'], \
                    })
                return jsonify({'id': new_id}), 201
        
        return jsonify({'message': 'Invalid token'}), 400
    
    elif request.method == 'GET':
        #Get request info
        location = request.args.get('location')
        if location is not None:
            location = location.lower()
        age_range = request.args.get('age_range')
        publisher_id = request.args.get('publisher_id')

        if publisher_id is None:
            return jsonify({'message': 'Missing required data'}), 400
        
        #validade publisher id
        isvalid = False
        cur2 = conn.cursor()
        #publishers = cur2.execute("SELECT * FROM users WHERE type = ?", ("C",)).fetchall()
        publishers = USERS_COLLECTION.find({"type": "C"})
        #cur2.close()
        for publisher in publishers:
            if str(publisher['id']) == str(publisher_id):
                isvalid = True
                break

        if not isvalid:
            return jsonify({'message': 'Invalid publisher id'}), 400

        if age_range not in age_groups and age_range:
            return jsonify({'message': 'Invalid age range'}), 400

        #cur3 = conn.cursor()
        if location is not None and age_range is not None:
            #ads = cur3.execute("SELECT * FROM ads WHERE location = ? AND age_range = ?", (location, age_range)).fetchall()
            ads = ADS_COLLECTION.find({"location": location, "age_range": age_range})
        elif location is not None:
            # ads = cur3.execute("SELECT * FROM ads WHERE location = ?", (location,)).fetchall()
            ads = ADS_COLLECTION.find({"location": location})
        elif age_range is not None:
            #ads = cur3.execute("SELECT * FROM ads WHERE age_range = ?", (age_range,)).fetchall()
            ads = ADS_COLLECTION.find({"age_range": age_range})
        else:
            #ads = cur3.execute("SELECT * FROM ads").fetchall()
            ads = ADS_COLLECTION.find({})
        # cur3.close()
        #select ads that are active
        ads = [ad for ad in ads if ad['active'] == 1]

        if len(ads) == 0:
            return jsonify({'message': 'No ads found'}), 404
        
        #selec a random ad
        ad = random.choice(ads)
        # print(ad)
        link = "http://"+HOST+"/v1/ads/" + str(ad['id'])
        if ad['pricing_model'] == "CPC":
            js_code = render_template('ad_cpc.html', ad_id=ad['id'], ad_creative=ad['ad_creative'], ad_description=ad['description'], ad_redirect=link)
        else:
            js_code = render_template('ad_cpm.html', ad_id=ad['id'], ad_creative=ad['ad_creative'], ad_description=ad['description'])

        #update impressions
        filter = {"id": ad['id']}
        newvalues = {"$set": {"impressions": ad['impressions'] + 1}}
        ADS_COLLECTION.update_one(filter, newvalues)

        #cur4 = conn.cursor()
        #cur4.execute("UPDATE ads SET impressions = ? WHERE id = ?", (ad['impressions'] + 1, ad['id']))
        #conn.commit()
        #cur4.close()
        if ad['pricing_model'] == "CPC":
            return jsonify({'ad': js_code, 'ad_id': ad['id'], 'ad_creative': ad['ad_creative'], 'ad_description': ad['description'], 'ad_redirect': link}), 200                
        else:
            return jsonify({'ad': js_code, 'ad_id': ad['id'], 'ad_creative': ad['ad_creative'], 'ad_description': ad['description']}), 200
    
    return jsonify({'message': 'Invalid request'}), 400
    

#Endpoint to create a new user account
@app.route('/v1/users', methods=['POST'])
def create_user():
    data = request.get_json()
    #validate data
    if 'name' not in data or 'email' not in data or 'password' not in data or 'type' not in data:
        return jsonify({'message': 'Missing required data'}), 400
    
    #check if email already exists
    #emails = cur.execute("SELECT email FROM users").fetchall()
    emails = USERS_COLLECTION.distinct("email")
    for email in emails:
        if email == data['email']:
            return jsonify({'message': 'Email already registered'}), 401

    #create new user
    #new_id = cur.execute("SELECT MAX(id) FROM users").fetchone()[0] + 1
    #check if there are any users
    if USERS_COLLECTION.count_documents({}) == 0:
        new_id = 1
    else:
        new_id = USERS_COLLECTION.find_one(sort=[("id", -1)])['id'] + 1
    print("New user id: " + str(new_id))
    token = get_token_from_request(new_id, data['email'], data['type'])
    type = data['type']
    #password_hash = hashlib.sha256(data['password'].encode('utf-8')).hexdigest()
    #cur.execute("INSERT INTO users (type, name, email, password, token) VALUES (?, ?, ?, ?, ?)", \
    #             (type, data['name'], data['email'], data['password'], token))
    USERS_COLLECTION.insert_one({"id": new_id, "type": type, "name": data['name'], "email": data['email'], "password": data['password'], "token": token})

    #conn.commit()
    return jsonify({'id': new_id}), 201

#Endpoint for authenticating an user
@app.route('/v1/login', methods=['POST'])
def login():
    data = request.get_json()
    #validate data
    if 'email' not in data or 'password' not in data:
        return jsonify({'message': 'Missing required data'}), 400
    #check if email and password match
    password = data['password'] 
    #password_hash = hashlib.sha256(password.encode('utf-8')).hexdigest()
    #cur.execute("SELECT * FROM users WHERE email = ? AND password = ?", (data['email'], password))
    # user = cur.fetchone()
    user = USERS_COLLECTION.find_one({"email": data['email'], "password": password})
   
    if user is not None:
        return jsonify({'token': get_token(user), 'id' : user['id'], 'name': user['name'], 'email': user['email'], 'type': user['type']}), 200

    return jsonify({'message': 'Invalid email or password'}), 401

#Endpoint to get the user profile and their ads
@app.route('/v1/profile', methods=['GET'])
def get_profile():
    global ADS_COLLECTION, USERS_COLLECTION
    token = request.headers.get('Authorization')
    if token is None:
        return jsonify({'message': 'Missing token'}), 401
    token = token.split(' ')[1]
    # users = cur.execute("SELECT * FROM users").fetchall()
    users = USERS_COLLECTION.find({})
    for user in users:
        if get_token(user) == token:
            #user_ads = cur.execute("SELECT * FROM ads WHERE user = ?", (user['id'],)).fetchall()
            user_ads = ADS_COLLECTION.find({"user": user['id']})
            print(user_ads)
            #transform each ad in a json
            ads_json = []
            for ad in user_ads:
                print(ad)
                tmp_json = {
                    'id': ad['id'],
                    'type': ad['type'],
                    'description': ad['description'],
                    'pricing_model': ad['pricing_model'],
                    'age_range': ad['age_range'],
                    'location': ad['location'],
                    'ad_creative': ad['ad_creative'],
                    'impressions': ad['impressions'],
                    'clicks': ad['clicks'],
                    'user': ad['user'],
                    'target': ad['target'],
                    'active': ad['active']
                }
                ads_json.append(tmp_json)
            return jsonify({
                'id': user['id'],
                'name': user['name'],
                'email': user['email'],
                'type': user['type'],
                'ads': ads_json
            }), 200
        
    return jsonify({'message': 'Invalid token'}), 401

#Endpoint to get the analytics for an ad
@app.route('/v1/analytics/ad/<int:ad_id>', methods=['GET'])
def get_ad_analytics(ad_id):
    global ADS_COLLECTION, USERS_COLLECTION
    #validate data
    token = request.headers.get('Authorization')
    if token is None:
        return jsonify({'message': 'Missing token'}), 401
    token = token.split(' ')[1]

    #check if user is advertiser
    if get_account_type(token) != "A":
        return jsonify({'message': 'Consumers unauthorized'}), 401

    if ad_id is None:
        return jsonify({'message': 'Missing required data'}), 400
    
    # user = cur.execute("SELECT * FROM users WHERE token = ?", (token,)).fetchone()
    user = USERS_COLLECTION.find_one({"token": token})
    if user is None:
        return jsonify({'message': 'Invalid token'}), 401
    
    #ad = cur.execute("SELECT * FROM ads WHERE id = ?", (ad_id,)).fetchone()
    ad = ADS_COLLECTION.find_one({'id': ad_id})
    if ad is None:
        return jsonify({'message': 'Ad not found'}), 404
    
    if ad['user'] != user['id']:
        return jsonify({'message': 'Unauthorized'}), 401
    
    return jsonify({
        'model': ad['pricing_model'],
        'impressions': ad['impressions'],
        'clicks': ad['clicks'],
        'ctr': round(ad['clicks']/ad['impressions']*100, 2)
    }), 200

#Endpoint to get the analytics for an advertiser
@app.route('/v1/analytics/advertiser/<int:adv_id>', methods=['GET'])
def get_user_analytics(adv_id):
    #validate data
    token = request.headers.get('Authorization')
    if token is None:
        print("Missing token")
        return jsonify({'message': 'Missing token'}), 401
    token = token.split(' ')[1]

    #check if user is advertiser
    if get_account_type(token) != "A":
        print("Consumers unauthorized")
        return jsonify({'message': 'Consumers unauthorized'}), 401

    if adv_id is None:
        return jsonify({'message': 'Missing required data'}), 400
    print("adv_id: " + str(adv_id))
    print("token: " + token)
    # user = cur.execute("SELECT * FROM users WHERE token = ? AND id = ?", (token, adv_id)).fetchone()
    user = USERS_COLLECTION.find_one({"token": token, "id": adv_id})
    if user is None:
        print("Invalid token or ID")
        return jsonify({'message': 'Invalid token or ID'}), 401
    
    #ads = cur.execute("SELECT * FROM ads WHERE user = ?", (adv_id,)).fetchall()
    ads = ADS_COLLECTION.find({'user': adv_id})

    if ads is None:
        return jsonify({'message': 'No ads found'}), 404

    #get the highest impression ad
    #highest_impression_ad = cur.execute("SELECT * FROM ads WHERE user = ? ORDER BY impressions DESC LIMIT 1", (adv_id,)).fetchone()
    highest_impression_ad = ADS_COLLECTION.find_one({'user': adv_id}, sort=[('impressions', -1)])

    #get the highest click ad
    #highest_click_ad = cur.execute("SELECT * FROM ads WHERE user = ? ORDER BY clicks DESC LIMIT 1", (adv_id,)).fetchone()
    highest_click_ad = ADS_COLLECTION.find_one({'user': adv_id}, sort=[('clicks', -1)])

    #get the total impressions
    #total_impressions = cur.execute("SELECT SUM(impressions) FROM ads WHERE user = ?", (adv_id,)).fetchone()[0]
    pipeline = [
        { "$match": { "user": adv_id } },
        { "$group": { "_id": None, "total_impressions": { "$sum": "$impressions" } } }
    ]
    total_impressions = ADS_COLLECTION.aggregate(pipeline).next()['total_impressions']
    

    #get the total clicks
    #total_clicks = cur.execute("SELECT SUM(clicks) FROM ads WHERE user = ?", (adv_id,)).fetchone()[0]
    pipeline = [
        { "$match": { "user": adv_id } },
        { "$group": { "_id": None, "total_clicks": { "$sum": "$clicks" } } }
    ]
    total_clicks = ADS_COLLECTION.aggregate(pipeline).next()['total_clicks']

    #get the total ctr as a float with 2 decimal places
    if(total_impressions == 0):
        total_ctr = 0
    else:
        total_ctr = round(total_clicks/total_impressions*100, 2)

    if(highest_impression_ad['impressions'] == 0):
        highest_impression_ctr = 0
    else:
        highest_impression_ctr = round(highest_impression_ad['clicks']/highest_impression_ad['impressions']*100, 2)

    if(highest_click_ad['impressions'] == 0):
        highest_click_ctr = 0
    else:
        highest_click_ctr = round(highest_click_ad['clicks']/highest_click_ad['impressions']*100, 2)
    return jsonify({
        'highest_impression_ad': {
            'id': highest_impression_ad['id'],
            'description': highest_impression_ad['description'],
            'model': highest_impression_ad['pricing_model'],
            'impressions': highest_impression_ad['impressions'],
            'clicks': highest_impression_ad['clicks'],
            'ctr': highest_impression_ctr,
            'ad_creative': highest_impression_ad['ad_creative'],
            'active': highest_impression_ad['active']
        },
        'highest_click_ad': {
            'id': highest_click_ad['id'],
            'description': highest_click_ad['description'],
            'model': highest_click_ad['pricing_model'],
            'impressions': highest_click_ad['impressions'],
            'clicks': highest_click_ad['clicks'],
            'ctr': highest_click_ctr,
            'ad_creative': highest_click_ad['ad_creative'],
            'active': highest_click_ad['active']
        },
        'total_impressions': total_impressions,
        'total_clicks': total_clicks,
        'total_ctr': total_ctr,
        'number_of_ads': len(list(ads))
    }), 200


if __name__ == '__main__':
    app.run(host='0.0.0.0',port=PORT, debug=True)

