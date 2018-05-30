import traceback
import time
import requests
import re
import MySQLdb
from bs4 import BeautifulSoup

class colouredOutput:
    OKBLUE = '\033[94m'
    OKGREEN = '\033[92m'
    WARNING = '\033[93m'
    FAIL = '\033[91m'
    ENDC = '\033[0m'
    BOLD = '\033[1m'

def loginWeb(er, pw):

    print(colouredOutput.OKGREEN + "Logging in to Web" + colouredOutput.ENDC)
    URL = "http://124.30.5.130/sms/login.aspx?ReturnUrl=%2fsms%2fAttendance.aspx"
    #URL = "http://192.168.9.52/sms/login.aspx?ReturnUrl=%2fsms%2f"
    headers = {"User-Agent":"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36"}

    session = requests.Session()
    session.headers.update(headers)
    r=session.get(URL)
    soup=BeautifulSoup(r.content, "html.parser")

    VIEWSTATE = soup.find(id="__VIEWSTATE")['value']
    EVENTVALIDATION = soup.find(id="__EVENTVALIDATION")['value']

    login_data={"__VIEWSTATE" : VIEWSTATE,
        "__EVENTVALIDATION" : EVENTVALIDATION,
        "ctl00$ContentPlaceHolder$txtEnrollmentNo" : er,
        "ctl00$ContentPlaceHolder$txtPassword" : pw,
        "ctl00$ContentPlaceHolder$btnSubmit" : "Submit"}

    r = session.post(URL, data=login_data)
    if r.url == "http://124.30.5.130/sms/Attendance.aspx":
        print("Logged in to Web!")
        return session
    else:
        print(colouredOutput.FAIL + "Error logging in!" + colouredOutput.ENDC)
        return ""

def subjectNames(session):
    urlSubject = "http://124.30.5.130/sms/SubjectwiseAttendance.aspx"
    #urlSubject = "http://192.168.9.52/sms/SubjectwiseAttendance.aspx"
    k = 0
    names = []

    subjectsSource = session.get(urlSubject)
    subjectSoup = BeautifulSoup(subjectsSource.content, "html.parser")
    for options in subjectSoup.findAll('option'):
        optionData = options.string
        optionData = optionData[ optionData.rfind(':') + 3 : ]
        names.insert(k, optionData)
        k = k + 1

    print("Logged out from web!")
    session.close()
    return names

def cumulative(session):
    urlCummulative = "http://124.30.5.130/sms/CummulativeAttendance.aspx"
    #urlCummulative = "http://192.168.9.52/sms/CummulativeAttendance.aspx"
    attendanceSource = session.get(urlCummulative)
    dataSoup = BeautifulSoup(attendanceSource.content, "html.parser")
    print("Fetching data!!!")
    attendanceFinal=dataSoup.find('div', {'class' : "Form"})
    attendanceFinal=str(attendanceFinal)
    attendanceFinal=re.escape(attendanceFinal)
    return attendanceFinal

def loginApp():
    print(colouredOutput.OKGREEN + "Logging in to App" + colouredOutput.ENDC)
    URL = "http://sms.iul.ac.in/Student/login.aspx"
    headers = {"User-Agent":"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36"}

    session = requests.Session()
    session.headers.update(headers)
    r=session.get(URL)
    soup=BeautifulSoup(r.content, "html.parser")

    VIEWSTATE = soup.find(id="__VIEWSTATE")['value']
    EVENTVALIDATION = soup.find(id="__EVENTVALIDATION")['value']

    login_data={"__VIEWSTATE" : VIEWSTATE,
        "__EVENTVALIDATION" : EVENTVALIDATION,
        "txtun" : er,
        "txtpass" : pw,
        "btnlog" : "LOGIN"}

    r = session.post(URL, data=login_data)
    if r.url == "http://sms.iul.ac.in/Student/index.aspx":
        print("Logged in to App!")
        return session
    else:
        print(colouredOutput.FAIL + "Error logging in!" + colouredOutput.ENDC)
        return ""

def iuSubj(session, names):
    k = 0

    subData = '<div class="Form">\n<div class="CenterTitle">\n<div class="Left">Subjective Attendance Report from 18/09/2017 to present date</div>'
    subData = subData + '\n<div class="Right">\n</div>\n</div>\n<br/>\n<table style="width:100%">\n<tr>\n<td colspan="6">'
    subData = subData + '\n<div>\n<table border="1" cellspacing="0" id="ContentPlaceHolder_grdCumulativeAttendance" rules="all" style="font-family:Verdana;width:100%;border-collapse:collapse;text-align: center">'
    subData = subData + '\n<tr>\n<th scope="col">Subject Code</th>\n<th scope="col">Subject Name</th>\n<th scope="col">Present</th>'
    subData = subData + '\n<th scope="col">Total</th>\n</tr>\n'

    subjUrl = "http://sms.iul.ac.in/Student/Attendance.aspx"
    source = session.get(subjUrl)
    soup = BeautifulSoup(source.content, "html.parser")
    print("Fetching data!!!")

    table=soup.find('table', {'class' : "table table-bordered table-striped"})
    sizeTableRows = len(table.findAll('tr'))
    for i in range(0, sizeTableRows-2):
        code = table.find('span', {'id' : 'ContentPlaceHolder1_rptrcontact_lbllst_'+str(i)})
        total = table.find('span', {'id' : 'ContentPlaceHolder1_rptrcontact_lblmob_'+str(i)})
        present = table.find('span', {'id' : 'ContentPlaceHolder1_rptrcontact_lblemail_'+str(i)})
        try:
            data = '<tr>\n<td>' + str(code.text.strip()) +'</td>\n<td>' + str(names[k]) +'</td>'
        except:
            data = '<tr>\n<td>' + str(code.text.strip()) +'</td>\n<td>' + str("----") +'</td>'
        k = k + 1
        data = data + '\n<td>' + str(present.text) +'</td>\n<td>' + str(total.text) +'</td>\n</tr>'
        subData = subData + data

    percentage = table.find('span', {'id' : 'ContentPlaceHolder1_rptrcontact_lblCumAtt'}).text

    subData = subData + '</table>\n</div>\n</td>\n</tr>\n<tr>\n<td colspan="4" style="float:right"> <b> Attendance Percentage : </b></td>'
    subData = subData + '\n<td colspan="2" style="width:12%"> <span id="ContentPlaceHolder_lblRelative" style="font-weight:bold;">' + str(percentage) + '</span></td>'
    subData = subData + '\n</tr>\n</table>\n</div>'
    subData = re.escape(subData)
    return subData

# ---- To update existing user's info ---- #
start = input("Enter start point: ")
end = input("Enter end point: ")
query = "SELECT * FROM users WHERE id >= "+str(start)+" AND id < "+str(end)

'''# ---- To update new user's info ---- #
query = "SELECT * FROM users WHERE html_data IS null"'''

'''# ---- To update a particular user's info ---- #
idUser = input("Enter id: ")
query = "SELECT * FROM users WHERE id = "+str(idUser)'''

errorId = []
errorIndex = 0
try:
    db = MySQLdb.connect("xx.xx.xx.xx", "root", "password", "clgdb", use_unicode=True, charset="utf8")
    print(colouredOutput.BOLD + "\nDatabase Connected" + colouredOutput.ENDC)
    cursor = db.cursor()
    cursor.execute(query)
    results = cursor.fetchall()
    er = ""
    pw = ""
    for row in results:
        id = row[0]
        er = row[1]
        pw = row[2]
        err = 0
        startTime = time.clock()
        print("\n---------------------------------")
        print("\n" + colouredOutput.BOLD + str(id) + "\t" + str(er) + "\t" + str(pw) + colouredOutput.ENDC)
        session = loginWeb(er, pw)
        cummulativeData = ""
        if session == "":
            # ---- The next line ensures that the error message does not gets updated to database ---- #
            cummulativeData = ""

            # ---- Uncomment the next line if you want error message to be updated to database ---- #
            #cummulativeData = "You have entered wrong Enrollment Number and password. Kindly update your credentials to view your attendance data "
            err = 1

        else:
            cummulativeData = cumulative(session)

        if (cummulativeData != ""):
            print("Updating data...")
            sql = 'UPDATE users SET html_data = \"' + str(cummulativeData) + '\" WHERE enrollment = \"' + str(er) + '\"'
            cursor.execute(sql)
            db.commit()
            if err == 0:
                print(colouredOutput.BOLD + colouredOutput.OKBLUE + "Cumulative updated!" + colouredOutput.ENDC)
            elif err == 1:
                print(colouredOutput.BOLD + colouredOutput.WARNING + "Cumulative error updated!" + colouredOutput.ENDC)

            if session != "":
                names = subjectNames(session)
            #----------------------------------------------
                sessionApp = loginApp()
                if len(str(sessionApp)) > 0:
                    data = iuSubj(sessionApp, names)
                    if data == "":
                        print(colouredOutput.FAIL + "Subjective updating skipped!" + colouredOutput.ENDC)
                    else:
                        print("Updating data...")
                        sql = 'UPDATE subj_att SET subj_data = \"' + data + '\" WHERE enrollment = \"' + str(er) + '\"'
                        cursor.execute(sql)
                        db.commit()
                        print(colouredOutput.BOLD + colouredOutput.OKBLUE + "Subjective updated!" + colouredOutput.ENDC)
                else:
                    print(colouredOutput.FAIL + "Error logging to App!" + colouredOutput.ENDC)
            #----------------------------------------------
        else:
            print(colouredOutput.FAIL + "Cumulative updating skipped!" + colouredOutput.ENDC)
            errorId.insert(errorIndex, id)
            errorIndex = errorIndex + 1
            if( errorIndex > 4 ):
                break
        elapsed = round( (time.clock() - startTime), 1 )
        print("Time taken: " + colouredOutput.BOLD + str(elapsed) + colouredOutput.ENDC + " seconds")
    db.close()

except:
    print(traceback.format_exc())
    print(colouredOutput.FAIL + "Database Connection Error")

print("\n---------------------------------")
if errorIndex > 0:
    if errorIndex > 4:
        print(colouredOutput.BOLD + colouredOutput.FAIL + "\nIU Server is currently down!" + colouredOutput.ENDC, end="")
    print("\nRerun for the following id's:-")
    for i in range(0, errorIndex):
        print(errorId[i])
else:
    print(colouredOutput.BOLD + colouredOutput.OKBLUE + "\nData successfully updated...!!!" + colouredOutput.ENDC)