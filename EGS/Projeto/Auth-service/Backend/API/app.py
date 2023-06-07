import os
import pathlib
from flask import Flask, request, session, abort, redirect, jsonify
import google
from google_auth_oauthlib.flow import Flow
from google.oauth2.credentials import Credentials
from google.oauth2 import id_token
from requests_oauthlib import oauth2_session
import cachecontrol
import requests
import re
from flask import Flask, render_template, url_for, redirect, jsonify
from flask_bcrypt import Bcrypt
from authlib.integrations.flask_client import OAuth
from flask_cors import CORS, cross_origin
import json
import os
import sqlite3
from flask import Flask, redirect, request, url_for
from flask_login import (
    LoginManager,
    current_user,
    login_required,
    login_user,
    logout_user,
)
from oauthlib.oauth2 import WebApplicationClient
import requests


app = Flask(__name__)

cors = CORS(app, resources={r"/api/*": {"origins": "*"}})
oauth = OAuth(app)

app.config['SECRET_KEY'] = "THIS SHOULD BE SECRET"
app.config['GITHUB_CLIENT_ID'] = "c5959263dd4c9d18f23c"
app.config['GITHUB_CLIENT_SECRET'] = "60d9d8b71bb724772bab2a35e5853980df0b9f09"
app.config['CORS_HEADERS'] = 'Content-Type'

github = oauth.register (
  name = 'github',
    client_id = app.config["GITHUB_CLIENT_ID"],
    client_secret = app.config["GITHUB_CLIENT_SECRET"],
    access_token_url = 'https://github.com/login/oauth/access_token',
    access_token_params = None,
    authorize_url = 'https://github.com/login/oauth/authorize',
    authorize_params = None,
    api_base_url = 'https://api.github.com/',
    client_kwargs = {'scope': 'user:email'},
)

app = Flask("Google Login App")
app.secret_key = "CodeSpecialist.com"
CORS(app)

GOOGLE_CLIENT_ID = "1024531629328-q5k5rjh7i55qbjjj5edv39capjorq1dh.apps.googleusercontent.com"
client_secrets_file = os.path.join(pathlib.Path(__file__).parent, "client_secret.json")
flow = Flow.from_client_secrets_file(client_secrets_file=client_secrets_file,
                                        scopes=["https://www.googleapis.com/auth/userinfo.email","https://www.googleapis.com/auth/userinfo.profile","openid"],
                                        redirect_uri="http://mixit-egs.duckdns.org/callback"
                                    )
                                        
# let me use only http for now
os.environ["OAUTHLIB_INSECURE_TRANSPORT"] = "1"

# this api must receive a request to make the authentication
# and then it will return the id_token
login_manager = LoginManager()
login_manager.init_app(app)

token = None

def login_required(function):
    def wrapper(*args, **kwargs):
        if "google_id" not in session:
            return abort(401)
        else:
            return function()
    return wrapper
        
# Default route
@app.route('/github')
def github_index():
  return redirect(url_for('github_login'))

# Github login route
@app.route('/login/github')
def github_login():
    github = oauth.create_client('github')
    redirect_uri = url_for('github_authorize', _external=True)
    return github.authorize_redirect(redirect_uri)


# Github authorize route
@app.route('/login/github/authorize')
def github_authorize():
    github = oauth.create_client('github')
    token = github.authorize_access_token()
    resp = github.get('/user')
    # print(f"\n{resp.json()}\n")
    user_info = resp.json()['login']
    user_id = resp.json()['id']

    with sqlite3.connect("database.db") as con:
        cur = con.cursor()
        # verify if the username and email already exists and return an error if it does
        cur.execute("SELECT * FROM users WHERE username = ? OR github_id= ?", (user_info, user_id))
        if cur.fetchone() is not None:
            return redirect("http://127.0.0.1:5100/auth?token="+token['access_token'])
        # if the user doesn't exist, add it to the database
        cur.execute("INSERT INTO users (username, password, github_id) VALUES (?,?,?)", (user_info, "github", user_id))
        con.commit()
        msg = "Record successfully added"
    con.close()

    if msg == "Record successfully added":
        return "Login Success", 200
    else:
        return "Login Failed", 401

@app.route("/")
def index():
    return redirect("/login")

@app.route("/login")
@cross_origin()
def login():
    authorization_url, state = flow.authorization_url()
    session["state"] = state
    return redirect(authorization_url)

@app.route("/callback")
def callback():
    flow.fetch_token(authorization_response=request.url)
    
    credentials= flow.credentials
    request_session= requests.session()
    cached_session= cachecontrol.CacheControl(request_session)
    token_request= google.auth.transport.requests.Request(session=cached_session)

    id_info = id_token.verify_oauth2_token(
        id_token=credentials._id_token,
        request=token_request,
        audience=GOOGLE_CLIENT_ID
    )
    locale = id_info.get('locale')
    family_name = id_info.get('family_name')
    given_name = id_info.get('given_name')
    picture = id_info.get('picture')
    email = id_info.get('email')
    name = id_info.get('name')
    email_verified = id_info.get('email_verified')
    sub = id_info.get('sub')
    hd = id_info.get('hd')

    user_info = jsonify({"email": email, "token": credentials._id_token})
       

    # connection.execute('CREATE TABLE users (username TEXT, password TEXT, email TEXT,google_id TEXT,github_id TEXT)')
    # create a new user with the google id name and email
    with sqlite3.connect("database.db") as con:
        cur = con.cursor()
        # verify if the username and email already exists and return an error if it does
        cur.execute("SELECT * FROM users WHERE username = ? OR email = ?", (name, email))
        if cur.fetchone() is not None:
            # redirect to the http://127.0.0.1:5100 with email and token
            return redirect("http://127.0.0.1:5100/auth?email="+email+"&token="+credentials._id_token)
            
        # if the user doesn't exist, add it to the database
        cur.execute("INSERT INTO users (username, password, email, google_id) VALUES (?,?,?,?)", (name, "google", email, sub))
        con.commit()
        msg = "Record successfully added"
    con.close()

    if msg == "Record successfully added":
        return "Register Success", 200
    else:
        return "Register Failed", 401
    

@app.route('/loginSubmit', methods=['POST'])
def loginSubmit():
    data = request.get_json()
    username = data['username']
    password = data['password']
    # check if the username and password is already is in database
    with sqlite3.connect("database.db") as con:
        sec = con.cursor()
        sec.execute("SELECT email FROM users WHERE username = ?", (username,))
        email = jsonify(sec.fetchone())
        cur = con.cursor()
        cur.execute("SELECT * FROM users WHERE username = ? AND password = ?", (username, password))
        # get the email from the database
        if cur.fetchone() is not None:
            return email, 200
        else:
            return "Login Failed", 401
        
@app.route('/user-page')
def user_page():
    email = request.args.get('email')
    user_mail={
        "email": email
    }
    return jsonify(user_mail), 200


@app.route('/registerSubmit', methods=['POST'])
def register():
    data = request.get_json()
    username = data['username']
    password = data['password']
    mail = data['email']

    with sqlite3.connect("database.db") as con:
        cur = con.cursor()
        # verify if the username and email already exists and return an error if it does
        cur.execute("SELECT * FROM users WHERE username = ? OR email = ?", (username, mail))
        if cur.fetchone() is not None:
            return "User already exists", 401
        # if the user doesn't exist, add it to the database
        cur.execute("INSERT INTO users (username, password, email) VALUES (?,?,?)", (username, password, mail))
        con.commit()
        msg = "Record successfully added"
    con.close()
    
    if msg == "Record successfully added":
        return "Register Success", 200
    else:
        return "Register Failed", 401
           

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5019, debug=True)
    