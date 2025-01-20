from flask import Flask, render_template, request, redirect, url_for, flash, session
import numpy as np
import mysql.connector
import re
import razorpay
import razorpay.errors
import gspread
from oauth2client.service_account import ServiceAccountCredentials

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

RAZORPAY_KEY_ID = "rzp_test_iXumXBu7UMOLEf"
RAZORPAY_KEY_SECRET = "DbnMUMaSxdlLkTNCZ0ruZb7R"

razorpay_client = razorpay.Client(auth=(RAZORPAY_KEY_ID,RAZORPAY_KEY_SECRET))

#landing
@app.route('/')
def home():
    if 'loggedin' in session:
        return render_template('landing.html')
    else:
        return redirect(url_for('login'))

#Diagnosis
@app.route('/diagnosis')
def diagnosis():
    if 'loggedin' in session:
        return render_template('diagnosis.html')
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
                session['firstname'] = user[3]
                session['membership'] = user[7]
                return redirect(url_for('home'))
            else:
                flash("Invalid login credentials", "error")
        else:
            #[SQL CONNECTION IS NULL]
            flash("Server Error, Please Try Later", "error")
    return render_template('login.html')

#Questionnaire1
@app.route('/startDiagnosis', methods=['POST'])
def startDiagnosis():
    if 'loggedin' in session:
        try:
            return(render_template('Questionnaire1.html',user=session['firstname']))
        except:
            flash("Some Error Occured","message")
            return redirect(url_for('login'))

    else:
        flash("Please login before diagnosis.","message")
        return(render_template('landing.html'))
    


#Questionnaire2  
@app.route('/questionnaire2', methods=['POST'])
def questionnaire2():
    try:
        answer1=[
                request.form['question1'],
                request.form['question2'],
                request.form['question3'],
                request.form['question4'],
                request.form['question5'],
                request.form['question6'],
                request.form['question7']]
    except:
        flash("Please fill every field","message")
        return(render_template('Questionnaire1.html',user=session['firstname']))
    session['answer']= answer1
    return(render_template('Questionnaire2.html',user=session['firstname']))

    
    
@app.route('/predict', methods=['POST'])
def predict():
    if request.method == 'POST':
        # Get user form input (Yes/No answers)
        try:
            answers2 = [
                request.form['question8'],
                request.form['question9'],
                request.form['question10'],
                request.form['question11'],
                request.form['question12'],
                request.form['question13'],
                request.form['question14']]
        except:
            flash("Please fill every field","message")
            return(render_template('Questionnaire2.html',user=session['username']))

        answers=[]
        answers.extend(session['answer']) 
        answers.extend(answers2)

        # Convert answers to numerical values (e.g., Yes = 1, No = 0)
        answers = [1 if answer == 'Yes' else 0 for answer in answers]

       
        user = {
            'is_member': True if session['membership'] == "active" else False
        }

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
        if disorder_scores[max(disorder_scores, key=disorder_scores.get)] !=0:
            most_likely_disorder = max(disorder_scores, key=disorder_scores.get)  
        else:
            most_likely_disorder = "Healthy"

        cur = sql_connection.cursor()
        cur.execute('SELECT * FROM responses WHERE user_id = %s', (session['user_id'],))
        if(cur.fetchone()):
            cur.execute('UPDATE responses SET disorder = %s WHERE user_id = %s',(most_likely_disorder,session['user_id']))
            sql_connection.commit()
        else:
            cur.execute('INSERT INTO responses (user_id, disorder) VALUES (%s, %s)', (session['user_id'],most_likely_disorder))
            sql_connection.commit()

        if(most_likely_disorder == "Healthy"):
            return render_template('healthy.html',name=session['firstname'])

        

        if(most_likely_disorder == "Anxiety Disorder"):
            raag = "Bilawal"
            TOD = "morning, during sunrise"

            song1 = "Mayur Pangkhi Louka Amar"
            link1 = "https://open.spotify.com/track/1QUefoT4WXG8tNR92ilCgE"

            song2 = "Manush Hoye"
            link2 = "https://open.spotify.com/track/1t715y57YZCksMYtNdtbIJ"

            song3 = "Bhatiganger Majhi Ami"
            link3 = "https://open.spotify.com/track/1APcdHREzjUAaqnJnlAIMG"

            desc = "Bilawal is a Shuddha raaga (all natural notes) that creates a cheerful, tranquil, and harmonious mood. Its simplicity makes it ideal for grounding the mind and alleviating anxious thoughts."

            firstAction = "Exercise: Regular physical activity releases endorphins and reduces stress hormones like cortisol. Activities like brisk walking, jogging, yoga, or swimming are especially beneficial."
            secondAction = "Deep Breathing Exercises: Practice diaphragmatic or box breathing to reduce anxiety symptoms immediately. For example, inhale for 4 seconds, hold for 4 seconds, exhale for 4 seconds, and repeat."
            thirdAction = "Progressive Muscle Relaxation (PMR): Tensing and then relaxing muscle groups can help reduce physical tension caused by anxiety."
            forthAction = "Watch Comedy: Laughter reduces stress hormones and boosts mood."
        elif(most_likely_disorder == "Depression"):
            raag = "Kafi"
            TOD = "evening"

            song1 = "Chikan Goalini"
            link1 = "https://open.spotify.com/track/6PKlzTQ4eA2N3Hrm8wPWIy"

            song2 = "Gyaner Gyanda"
            link2 = "https://open.spotify.com/track/1gj3lFcZW8vZkg9gW5c8Pl"

            song3 = "Vadu Amar Garobini"
            link3 = "https://open.spotify.com/track/4SSfCXsyYHGunfSUryzE1U"

            desc = "Raaga Kafi is soft and soothing, it creates a relaxed and pleasant atmosphere. It helps ease emotional pain, creating a sense of comfort and calm."

            firstAction = "Daily Exercise: Even light physical activity like walking, stretching, or yoga can release endorphins and improve mood. Aim for 20-30 minutes most days."
            secondAction = "Spending Time Outdoors: Activities like gardening or hiking expose you to sunlight, increasing Vitamin D and improving mood."
            thirdAction = "Gratitude Journaling: List 3 things you are grateful for each day to shift focus toward the positive aspects of life."
            forthAction = "Create a Sleep Routine: Go to bed and wake up at the same time daily, aiming for 7-9 hours of quality sleep."
        elif(most_likely_disorder == "Bipolar Disorder"):
            raag = "Poorvi"
            TOD = "twilight, i.e around dusk"

            song1 = "O Sundar"
            link1 = "https://open.spotify.com/track/3VdsY4zZweY9ijNOPIab09"

            song2 = "Khat Palanke"
            link2 = "https://open.spotify.com/track/6SB6rcD1j0F5mDdII2u2Y2"

            song3 = "Emon Manob Somaj"
            link3 = "https://open.spotify.com/track/1XDM4SFKl1dgK3vy6u2sb5"

            desc = "Poorvi is a Sandhi Prakash Raaga (suitable for twilight) and has a mystical quality that induces balance and emotional tranquility. It uses a mix of komal (flat) and shuddha (natural) notes, creating a meditative and grounding effect."

            firstAction = "Daily Schedule: Maintain a consistent routine for sleeping, eating, and activities to reduce mood swings triggered by irregularities."
            secondAction = "Positive Affirmations: Practice self-compassion with affirmations like, “I am in control of my thoughts,” or “This phase will pass.”"
            thirdAction = "Grounding with Nature: Touching the earth (e.g., gardening, sitting on grass) can help stabilize energy and connect to the present moment."
            forthAction = "Writing: Journaling thoughts and feelings fosters self-awareness and provides emotional release."
        elif(most_likely_disorder == "Obsessive Compulsive Disorder"):
            raag = "Kafi"
            TOD = "evening"

            song1 = "Boli O Khokar Ma"
            link1 = "https://open.spotify.com/track/13tdWCj9rxHx7UqiNXL7Mt"

            song2 = "Hari Din To Gelo"
            link2 = "https://open.spotify.com/track/1ijtcd8LuYfI3fVLvtqNcT"

            song3 = "Bhatiyal Ganger Naiya"
            link3 = "https://open.spotify.com/track/5MPm6CXwqH9x3eBS0xPJkr"

            desc = "Kafi is mellow and thus promotes relaxation and reduces tension. It eases anxiety and encourages mindfulness, reducing the compulsion to perform repetitive behaviors."

            firstAction = "Puzzles or Board Games: Activities that engage problem-solving can distract the mind from intrusive thoughts."
            secondAction = "Hobbies: Engage in hobbies you enjoy, such as knitting, photography, or woodworking, to occupy your hands and mind."
            thirdAction = "Limit Overchecking: Create boundaries for actions like re-reading, re-checking, or seeking excessive reassurance."
            forthAction = "Digital Detox: Avoid spending too much time online researching fears or compulsions, as this can fuel anxiety."
        elif(most_likely_disorder == "Post-Traumatic Stress Disorder"):
            raag = "Todi"
            TOD = "morning, just after sunrise"

            song1 = "Hatey Hari Ebar Ami"
            link1 = "https://open.spotify.com/track/3DMR2iKoWQ0SkIxlJ3Yfr8"

            song2 = "Bhangor Bhola Shib Tomar"
            link2 = "https://open.spotify.com/track/3msn6YUhZTpiLDiY0CCw5w"

            song3 = "Ghum Venge Besh Moja Hoeche"
            link3 = "https://open.spotify.com/track/4jwBPXsdUK9bI3aS6WodWA"

            desc = "Todi is a profound and introspective raaga that uses komal (flat) notes and a slow progression, creating a melancholic yet healing atmosphere. It encourages emotional release and processing of suppressed trauma as well as provides a grounding effect, counteracting feelings of fear and hypervigilance."

            firstAction = "5-4-3-2-1 Method: Identify five things you can see, four you can touch, three you can hear, two you can smell, and one you can taste to stay grounded in the present."
            secondAction = "Deep Breathing: Practice slow, deep breaths to reduce anxiety. For example, inhale for 4 counts, hold for 4, exhale for 4, and hold for 4 (box breathing)."
            thirdAction = "Morning Rituals: Start the day with a calming activity, such as journaling or stretching, to set a positive tone."
            forthAction = "Nighttime Wind-Down: Create a bedtime routine involving calming activities, such as reading or drinking herbal tea, to improve sleep quality."
        elif(most_likely_disorder == "Schizophrenia"):
            raag = "Kalyan"
            TOD = "evening, just after sunset"

            song1 = "Bare Bare Aar Asa Hobena"
            link1 = "https://open.spotify.com/track/1ZRHOQ2Ciikozn4jrruJmp"

            song2 = "Lal Ke Keno Bhoy"
            link2 = "https://open.spotify.com/track/607li9G8YMzWrdydgI9OcB"

            song3 = "Loke Bole Lalon Fakir"
            link3 = "https://open.spotify.com/track/06sQAV5rnZMqWU3tEdRRoZ"

            desc = "Raaga Kalyan is known for its majestic and calming essence. It combines natural and harmonic notes that create a soothing and contemplative atmosphere. The calming melodies can ease episodes of restlessness or anxiety, and encourages focus and reduces intrusive or fragmented thoughts. However we also suggest that along with listening to these songs you seek professional help and therapy support."

            firstAction = "Family and Friends: Maintain close relationships with trusted individuals who provide emotional support."
            secondAction = "Reading or Audiobooks: Explore uplifting or educational material to stay mentally active."
            thirdAction = "Establishing Hygiene Routines: Create a checklist for daily self-care tasks like bathing, grooming, and dressing."
            forthAction = "Cognitive Behavioral Therapy (CBT): Work with a therapist to address delusional thinking or distressing emotions."

        #Adding the latest predicted data in responses table
        


        session['premium'] = user
        session['disorder'] = most_likely_disorder
        session['link1'] = link1
        session['link2'] = link2
        session['link3'] = link3
        session['desc'] = desc
        session['a1'] = firstAction
        session['a2'] = secondAction
        session['a3'] = thirdAction
        session['a4'] = forthAction
        session['s1'] = song1
        session['s2'] = song2
        session['s3'] = song3
        session['raag'] = raag
        session['tod'] = TOD





        # Pass prediction to result page
        return render_template('result.html',name=session['firstname'],user=user, prediction=most_likely_disorder,link1=link1,link2=link2,link3=link3,description=desc,actions1=firstAction,actions2=secondAction,actions3=thirdAction,actions4=forthAction,song1=song1,song2=song2,song3=song3, raaga=raag,timeOfDay=TOD)
    
    
     # membership
@app.route('/membership', methods=['POST'])
def membership():
        if request.method == 'POST':    
            return render_template('membership.html',key_id=RAZORPAY_KEY_ID)

@app.route('/verify', methods=['POST'])
def verify_payment():
    #Get Data From Razorpay checkout
        payment_id = request.form.get("razorpay_payment_id")
        order_id = request.form.get("razorpay_order_id")
        signature = request.form.get("razorpay_signature")
        #Verify signature
        try:
            razorpay_client.utility.verify_payment_signature({
                "razorpay_payment_id": payment_id,
                "razorpay_order_id": order_id,
                "razorpay_signature": signature
            })
            #Update membership status
            user_id = session['user_id']
            cur = sql_connection.cursor()
            cur.execute('UPDATE users SET membership = "active" WHERE id = %s', (user_id,))
            sql_connection.commit()
            session['membership'] = "active"
            flash("Membership activated successfully!", "success")
            #return redirect(url_for('home'))
            return render_template('result.html',name=session['firstname'],user={'is_member':True}, prediction=session['disorder'],link1=session['link1'],link2=session['link2'],link3=session['link3'],description=session['desc'],actions1=session['a1'],actions2=session['a2'],actions3=session['a3'],actions4=session['a4'],song1=session['s1'],song2=session['s2'],song3=session['s3'], raaga=session['raag'],timeOfDay=session['tod'])
        except razorpay.errors.SignatureVerificationError:
            flash("Signature verification failed", "error")
            return render_template('membership.html',key_id=RAZORPAY_KEY_ID)

        
@app.route('/order', methods=['POST'])
def create_order():
    if 'loggedin' in session:
        amount = 9900 #In Paise
        currency = "INR"

        order_data = {"amount":amount,
                      "currency":currency }
        razorpay_order = razorpay_client.order.create(data=order_data)
        return{"order_id":razorpay_order['id'],"amount":amount}
    else:
        flash('Please log in to purchase membership.', 'error')
        return redirect(url_for('login.html'))

# FOR ADMIN PURPOSES
def get_google_form_responses():
    scope = ['https://www.googleapis.com/auth/spreadsheets.readonly']
    creds = ServiceAccountCredentials.from_json_keyfile_name('credentials.json', scope)
    client = gspread.authorize(creds)
    
    # Open the Google Sheet
    sheet = client.open_by_key('1wk1YHybbJMZl7iDLeukzgpEL2DLd2IDcWDwU8AjMH50').sheet1
    feedback_data = sheet.get_all_records()  # Get all form responses
    return feedback_data

@app.route('/admin_login', methods=['GET', 'POST'])
def admin_login():
    return render_template('admin_login.html')


@app.route('/admin', methods=['POST'])
def admin():
    admin_user = request.form['admin_username']
    admin_pass = request.form['admin_password']

    if(admin_user != "admin" or admin_pass != "admin12345"):
        return redirect(url_for('login'))
    session['admin_logged_in'] = True
    if 'admin_logged_in' not in session:
        return redirect(url_for('login'))
    
    # Fetch current users from the database
    cursor = sql_connection.cursor(dictionary=True)
    cursor.execute("SELECT users.id, users.firstname, users.lastname, users.emailId, users.phoneNumber, users.membership, responses.disorder FROM users JOIN responses ON users.id = responses.user_id")  # Update query as per your database schema
    users = cursor.fetchall()
    
    # Fetch feedback data from Google Forms
    feedback_data = get_google_form_responses()
    
    return render_template('admin.html', users=users, feedback_data=feedback_data)


if __name__ == '__main__':
    app.run(debug=True)
