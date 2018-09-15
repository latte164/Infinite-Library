from splinter import Browser
from firebase import firebase
import time
import re
import base64
import requests

#initialize firebase
firebase = firebase.FirebaseApplication("https://infinite-library.firebaseio.com", None)

#get the exe path on my computer for Chrome
executable_path = {'executable_path': 'C:\\Users\\vedva\\Downloads\\chromedriver_win32\\chromedriver.exe'}

#encodes an image at a URL into a base 64 string
def URLtoString(url):
	return base64.b64encode(requests.get(url).content).decode('utf-8')



"""
Get functions for all aspects of a book needed
Browser should already be visiting the signed URL page for these
They get a reference to the relevant HTML tags which I initially found manually
They then gather the necessary data and return it
"""
def getTitle(browser):
	try:
		title = browser.find_by_text('<Title').find_by_xpath('..').find_by_tag('span')[1].value
		if title == '<Title>':
			return browser.find_by_text('<Title').find_by_xpath('..').find_by_xpath('..').find_by_tag('div')[1].find_by_tag('span')[0].value
		else:
			return title
	except:
		print('An error occured in getting the title.')
		return 'Title Not Available'
def getAuthor(browser):
	try:
		author = browser.find_by_text('<Author').find_by_xpath('..').find_by_tag('span')[1].value
		if author == '<Author>':
			return browser.find_by_text('<Author').find_by_xpath('..').find_by_xpath('..').find_by_tag('div')[1].find_by_tag('span')[0].value
		else:
			return author
	except:
		print('An error occured in getting the author.')
		return 'Author Not Available'
def getDescription(browser):
	try:
		section = browser.find_by_text('Product Description').find_by_xpath('..').find_by_xpath('..')[0].html

		#gets the actual description, as the whole thing is strangely in an iframe document
		match = re.search(r'<Content>.*?</Content>', section).group()
		inner = match[9:len(match)-10]
		inner = inner.replace('&gt;','>').replace('&lt;','<')

		#remove keywords which might indicate that the description is actually of physical quality rather than content
		keywords = ['good', 'fine', 'best', 'used', 'quality','shipping', 'thumb marked']
		if any([x in inner.lower() for x in keywords]):
			print('Description about physical quality of book; was voided.')
			return 'Description Not Available'
		else:
			return inner
	except Exception as e:
		print('An error occured in getting the description: ', e)
		return 'Description Not Available'
def getPublisher(browser):
	try:
		publisher = browser.find_by_text('<Publisher').find_by_xpath('..').find_by_tag('span')[1].value
		if publisher == '<Publisher>':
			return browser.find_by_text('<Publisher').find_by_xpath('..').find_by_xpath('..').find_by_tag('div')[1].find_by_tag('span')[0].value
		else:
			return publisher
	except:
		print('An error occured in getting the publisher.')
		return 'Publisher Not Available'
def getPublicationDate(browser):
	#This section tries two different paths, as the publication date can be stored under either
	try:
		publicationDate = browser.find_by_text('<ReleaseDate').find_by_xpath('..').find_by_tag('span')[1].value
		if publicationDate == '<ReleaseDate>':
			return browser.find_by_text('<ReleaseDate').find_by_xpath('..').find_by_xpath('..').find_by_tag('div')[1].find_by_tag('span')[0].value
		else:
			return publicationDate
	except:
		try:
			publicationDate2 = browser.find_by_text('<PublicationDate').find_by_xpath('..').find_by_tag('span')[1].value
			if publicationDate2 == '<PublicationDate>':
				return browser.find_by_text('<PublicationDate').find_by_xpath('..').find_by_xpath('..').find_by_tag('div')[1].find_by_tag('span')[0].value
			else:
				return publicationDate2
		except:
			print('An error occured in getting the publication date.')
			return 'Date Published Not Available'
def getImageURL(browser):
	try:
		return browser.find_by_text('<LargeImage').find_by_xpath('..').find_by_xpath('..').find_by_tag('div')[5].value
	except:
		print('An error occured in getting the image URL.')
		return "Cover Image Not Available"

"""
Browser can be visiting anywhere or nowhere
Takes a browser and an ISBN and returns the signed URL for the book in question
"""
def getSignedURL(browser, isbn):
	#visits the signed URL calculator
	url = 'https://associates-amazon.s3.amazonaws.com/signed-requests/helper/index.html'
	browser.visit(url)

	#sets pre-defined values and elements on the page
	unsignedURL = 'http://webservices.amazon.com/onca/xml?AssociateTag=latte164-20&AWSAccessKeyId=AKIAJ2WUZBZZRLTAPGXQ&IdType=ISBN&ItemId='+isbn+'&Operation=ItemLookup&ResponseGroup=Large&SearchIndex=All&Service=AWSECommerceService'
	unsignedBox = browser.find_by_id('UnsignedURL').first

	accessKey = 'AKIAJHDM5VTNXMLPUSIQ'
	secretKey = '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'
	accessKeyBox = browser.find_by_id('AWSAccessKeyId').first
	secretKeyBox = browser.find_by_id('AWSSecretAccessKey').first

	signedBox = browser.find_by_id('SignedURL').first

	button = browser.find_link_by_href('javascript:invokeRequest();').first

	#fills the elements with the predefined data
	accessKeyBox.fill(accessKey)
	secretKeyBox.fill(secretKey)
	unsignedBox.fill(unsignedURL)

	#clicks the button to calculate the signed URL
	button.click()

	#returns the calculated URL
	return signedBox.value

"""
Takes an ISBN and an optional Dewey Decimal Number
Returns the book JSON object
"""
def getBookObj(isbn, deweyNum='Dewey Index Not Available'):
	#sets empty defaults for each field
	description = ''
	title = ''
	author = ''
	publisher = ''
	publicationDate = ''

	#initializes my chrome browser
	with Browser('chrome', **executable_path) as browser:
		#get and visit the signed URL for the book
		signedURL = getSignedURL(browser, isbn)
		print(signedURL)
		browser.visit(signedURL)

		#get all relevant fields
		title = getTitle(browser)
		author = getAuthor(browser)
		description = getDescription(browser)
		publisher = getPublisher(browser)
		publicationDate = getPublicationDate(browser)
		imageURL = getImageURL(browser)
		if imageURL == "Cover Image Not Available":
			image = "Cover Image Not Available"
		else:
			image = URLtoString(getImageURL(browser))

	#create the book JSON object
	bookObj = {	'Title': title, 
				'Author': author, 
				'Description': description, 
				'Publisher': publisher, 
				'Date Published': publicationDate, 
				'Cover': image,
				'Current Owner': 'Not Currently Checked Out',
				'Dewey Index Number': deweyNum,
				'Publication Location': 'Publication Location Not Available',
				'Rating': -1,
				'Rating Count': -1,
				'Reserved To': 'Not Reserved'
				}
	return bookObj

#simple function to upload a JSON object for a book to firebase
def uploadObj(obj, isbn):
	firebase.put('Books', isbn, obj)

#cycle through our school library's database of books and uploads valid entries to the database
with open('Marc - Copy.txt', 'r', encoding='utf8') as file:
	#splits the database into an array of book entries
	workable_text = file.read()
	workable_text = workable_text.split('ST=c')

	#I don't know why this is necessary but it is, python is weird sometimes
	workable_text = workable_text[0:45000]

	#cycles through the books
	#counter is used to measure the number of total book entries looked at (only fully fleshed out entries will be pushed to firebase)
	counter = 0
	for book in workable_text:
		counter+=1
		deweyNum = 'Dewey Index Not Available'
		isbn = ''
		#cycles through each row of the book entry looking for key information
		#the row that contains the ISBN starts with 020, Dewey Decimal Number is 082
		for row in book.split('\n'):
			if row[0:3] == '082':
				deweyNum = row[20::]
				if not deweyNum.replace('.','').isdigit():
					deweyNum = 'Dewey Index Not Available'
			if row[0:3] == '020':
				isbn = row[20::]
				if not (len(isbn) == 10 and isbn.isdigit()):
					isbn = ''
		#checks if the book actually had an ISBN and dewey decimal number listed
		if isbn!='' and deweyNum!='Dewey Index Not Available':
			#gets the book JSON object
			bookObj = getBookObj(isbn, deweyNum)
			#if there was enough information on Amazon, uploads the book to firebase
			if bookObj['Title'] != 'Title Not Available' and bookObj['Description'] != 'Description Not Available' and bookObj['Author'] != 'Author Not Available' and bookObj['Publisher'] != 'Publisher Not Available' and bookObj['Date Published'] != 'Date Published Not Available':
				uploadObj(bookObj, isbn)

	#prints the results of the search
	#32 entries out of 2082 total entries were valid in both our school's database and Amazon
	#Yay for complete records, amirite??
	print("Counter: ", counter)