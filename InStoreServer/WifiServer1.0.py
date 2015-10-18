from klein import Klein
import utils, StringIO, qrcode
from urllib2 import Request, urlopen, URLError
import json
import re

app = Klein()

"""
	customers: {
				<gid>:
					  {
							"Resolved": [(item, qty, unit_price), (item, qty, unit_price)...]
							"Unresolved": [(item1,qty1),(item2,qty2),...]
							"Total": ""
					  }
			   }
"""
customers = {}


@app.route('/')
def hello(request):
	'''
		Test API working with this
	'''
	return "Hello"

@app.route('/api/nearest/<lat>/<lon>')
def find_nearest_three(request,lat,lon):
	'''
		Using the user's location, find the three nearest Walmart Stores to visit
		Returns coords as <lat1>,<lon1>;<lat2>,<lon2>;... if all goes well
		Else: returns None
	'''
	try:
		if type(eval(lat)) == type(0.00) and type(eval(lon)) == type(0.00):
			request = Request('http://api.walmartlabs.com/v1/stores?apiKey={API_KEY}&lon='+lon+'&lat='+lat) # Nearest Stores
			response = urlopen(request)
			stuff = response.read()
			obj = json.loads(stuff)
			coords = ""
			for i in obj:
				ll = str(i["coordinates"])[1:-1]
				coords += ll+";"
			return coords
		else:
			return None
	except URLError, e:
	    return None

@app.route('/api/instantiate/<gid>')
def instantiate(request,gid):
	'''
		Registers the user when he/she enters the store and connects to the open local wireless network.
		Returns 0 if user already exists (online server)
		Returns 1 if user did not exist, but local session is registered
		Returns 2 if operation was unsuccessful
	'''
	try:
		if gid in customers.keys():
			return "0"
		else:
			customers[gid] = dict()
			return "1"
	except Exception:
		return "2"

@app.route('/api/updatelist/<gid>', methods = ['POST'])
def update_list(request, gid):
	'''
		
	'''
	data = request.args.get('items',"None")
	data = data[0][:-1].strip().split(';')
	#print data
	res = []
	itemIDs = []
	tot = 0.0
	unres = []
	for i in data:
		j = i.strip().split(',')
		#print j[0]
		#num, entries = utils.find_prod(j[0])
		request = Request('http://api.walmartlabs.com/v1/search/?apiKey={API_KEY}&query='+re.sub(r" ","%20",j[0]))
		try:
			response = urlopen(request)
			stuff = response.read()
			obj = json.loads(stuff)
			#print obj
			if int(obj['totalResults'])==0:
				unres.append((j[0],j[1]))
			else:
				itemIDs.append([j[0],j[1],obj['items'][0]['itemId']])
		except Exception as e:
			print e
	temp = []
	ids = ""
	for ID in itemIDs:
		ids += str(ID[2])+','
	print ids
	request = Request('http://api.walmartlabs.com/v1/items?ids='+ids[:-1]+'&apiKey={API_KEY}')
	try:
		response = urlopen(request)
		stuff = response.read()
		#print stuff
		obj = json.loads(stuff)
		#print obj
		for k in range(len(obj["items"])):
			itemIDs[k][0] = obj["items"][k]["name"]
			itemIDs[k][2] = float(obj["items"][k]["salePrice"])
			tot += float(itemIDs[k][2]) * float(itemIDs[k][1])
			res.append(itemIDs[k])
	except URLError, e:
		print "Error in updatelist"
	customers[gid]["Resolved"] = res[:]
	customers[gid]["Unresolved"] = unres[:]
	customers[gid]["Total"] = tot
	s = ""
	for i in unres:
		s+=str(i[0])+','+str(i[1])+';'
	#print customers
	return s


@app.route('/api/getresolved/<gid>')
def get_resolved(request, gid):
	s = ""
	for i in customers[gid]["Resolved"]:
		s+=str(i[0])+','+str(i[1])+','+str(i[2])+';'
	return s

@app.route('/api/fetchmap/<gid>')
def serve_map(request, gid):
	pass
	

@app.route('/api/cost/<gid>/<item>')
def return_cost(request, gid, item):
	for i in customers[gid]["Resolved"]:
		if item == i[0]:
			return '$'+str(float(i[1]) * float(i[2]))
	

@app.route('/api/costn/<gid>')
def return_costn(request, gid):
	return '$'+str(customers[gid]["Total"])


@app.route('/api/checkout/<gid>')
def checkout(request, gid):
	'''
		Creates a QR code for the maintained object state
	'''
	request.setHeader('Content-type','image/png')
	if gid in customers.keys():
		data = customers[gid]
		image = qrcode.make(data)
		output = StringIO.StringIO()
		image.save(output)
		return output.getvalue()
	else:
		return None
	

@app.route('/api/update/<gid>/<option>/<item>/<qty>')
def update(request, gid, option, item, qty):
	'''
	'''
	if option == "add":
		pass
	elif option == "delete":
		for i in customers[gid]["Resolved"]:
			if i[0] == item:
				i[1] = float(i[1]) - float(qty)
				i[2] = i[1] * i[2]
				if i[2] == 0.0:
					customers[gid]["Resolved"].remove(i)
				break
		return "true"
	else:
		return None
	
if __name__ == "__main__":
   app.run("0.0.0.0", 5000)
