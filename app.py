from flask import Flask, render_template, request, redirect, url_for, flash, session
import numpy as np
from joblib import load
import mysql.connector
import re

# Initialize Flask app
app = Flask(__name__)
app.secret_key = 'secret_key'

# Database setup
sql_connection = mysql.connector.connect(
        host = "115.187.17.57",
        user = "debanjan",
        password = "debanjan",
        database = "flask_ml_db",
        port = "3316"
    )
# Load ML model
#model = load('model.sav')

# Home Page
@app.route('/')
def home():
    if 'loggedin' in session:
        return render_template('home.html')
    else:
        return redirect(url_for('login'))

#Register
@app.route('/register', methods=['GET', 'POST'])
def register():
    if request.method == 'POST':
        username = request.form['username']
        password = request.form['password']
        firstName = request.form['firstName']
        lastName = request.form['lastName']
        phoneNumber = request.form['phoneNumber']
        emailId = request.form['emailId']
        password_regex = r'^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,30}$'
        if not re.match(password_regex, password):
            flash("Password must be atleast 8 characters long and at max be 30 characters,\nIncludes at least one uppercase letter, one lowercase letter, one number, and one special character.", "error")
            return render_template('register.html')
        if not re.match(r'^[6-9]\d{9}$', phoneNumber):
            flash("Invalid phone number. Please enter a valid phone number.", "error")
            return render_template('register.html')
        if emailId.startswith('_'):
            flash("Email cannot start with an underscore. Try Again.", "error")
            return render_template('register.html')
        if(re.match(r'^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$',emailId)):
            if(sql_connection):
                
                cur = sql_connection.cursor()
                cur.execute('INSERT INTO users (username, password, firstName, lastName, phoneNumber, emailId) VALUES (%s, %s, %s, %s, %s, %s)', (username, password, firstName, lastName, phoneNumber, emailId))
                sql_connection.commit()
                flash('You have successfully registered', 'success')
                return redirect(url_for('login'))
            else:
                print("connect is null")
        else:
            flash("Invalid Email. Try Again","error")
    return render_template('register.html')

#Logout Page
@app.route('/logout', methods=['POST'])
def logout():
    session.pop('user',None)
    flash("Logged out successfully!", "logout")
    return redirect(url_for('login'))

# Login Page
@app.route('/login', methods=['GET', 'POST'])
def login():
    if request.method == 'POST':
        username = request.form['username']
        password = request.form['password']
        if(sql_connection):
            cur = sql_connection.cursor()
            cur.execute('SELECT * FROM users WHERE username = %s AND password = %s', (username, password))
            user = cur.fetchone()
            if user:
                session['loggedin'] = True
                session['username'] = username
                session['user_id'] = user[0]
                return redirect(url_for('home'))
            else:
                flash("Invalid login credentials", "error")
        else:
            print("Connect is null")
    return render_template('login.html')

#Questionnaire1
@app.route('/startDiagnosis', methods=['POST'])
def startDiagnosis():
    if 'loggedin' in session:
        return(render_template('Questionnaire1.html'))
    else:
        flash("Please login before diagnosis.","message")
        return(render_template('home.html'))
    
#################################################################
# #Questionnaire2  
# @app.route('/questionnaire2', methods=['POST'])
# def questionnaire2():
#     answer1=[request.form['question1'],
#             request.form['question2'],
#             request.form['question3'],
#             request.form['question4'],
#             request.form['question5'],
#             request.form['question6'],
#             request.form['question7'],
#             request.form['question8'],
#             request.form['question9'],
#             request.form['question10'],
#             request.form['question11'],
#             request.form['question12'],
#             request.form['question13']]
#     session['answer']= answer1
#     return(render_template('Questionnaire2.html'))
############################################################

#Questionnaire2  
@app.route('/questionnaire2', methods=['POST'])
def questionnaire2():
    answer1=[request.form['question1'],
            request.form['question2'],
            request.form['question3'],
            request.form['question4'],
            request.form['question5'],
            request.form['question6'],
            request.form['question7']]
    session['answer']= answer1
    return(render_template('Questionnaire2.html'))

    
    
# Result Route (Display prediction)
@app.route('/result')
def result():
    return render_template('result.html')

# # Prediction API (to interact with the ML model)
# @app.route('/predict', methods=['POST'])
# def predict():
#     if request.method == 'POST':
#         # Get user form input (Yes/No answers)
#         answers2 = [
#             request.form['question14'],
#             request.form['question15'],
#             request.form['question16'],
#             request.form['question17'],
#             request.form['question18'],
#             request.form['question19'],
#             request.form['question20'],
#             request.form['question21'],
#             request.form['question22'],
#             request.form['question23'],
#             request.form['question24'],
#             request.form['question25'],
#             request.form['question26'],
#             request.form['question27']
#         ]
#         answers=[]
#         answers.extend(session['answer']) 
#         answers.extend(answers2)

############################################################
# Prediction API (to interact with the ML model)
@app.route('/predict', methods=['POST'])
def predict():
    if request.method == 'POST':
        # Get user form input (Yes/No answers)
        answers2 = [
            request.form['question8'],
            request.form['question9'],
            request.form['question10'],
            request.form['question11'],
            request.form['question12'],
            request.form['question13'],
            request.form['question14']]

        answers=[]
        answers.extend(session['answer']) 
        answers.extend(answers2)

        # Convert answers to numerical values (e.g., Yes = 1, No = 0)
        answers = [1 if answer == 'Yes' else 0 for answer in answers]

        #####################################################################
        # # Convert to numpy array and reshape for the model
        # features = np.array(answers).reshape(1, -1)
        
        # # Predict using the ML model
        # prediction = model.predict(features)
        
        # # Return the result to the user
        # return render_template('result.html', prediction=prediction[0])
        #####################################################################

        # Logic to calculate disorder scores

        disorder_scores = {
            "Anxiety Disorder": answers[0] + answers[9] + answers[13],
            "Depression": answers[1] + answers[8] + answers[12] ,
            "Bipolar Disorder": answers[2] + answers[10],
            "Obsessive Compulsive Disorder": answers[3] + answers[11],
            "Post-Traumatic Stress Disorder": answers[4] + answers[5],
            "Schizophrenia":  answers[6] + answers[12]
        }

        # Determine the most likely disorder
        most_likely_disorder = max(disorder_scores, key=disorder_scores.get)

        # Pass prediction to result page
        return render_template('result.html', prediction=most_likely_disorder)


if __name__ == '__main__':
    app.run(debug=True)
