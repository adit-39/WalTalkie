from semantics3 import Products

products = Products(
  api_key = "SEM**************************",
  api_secret = "Nj**************************"
)

def find_prod(prod):
	products.products_field( "search", prod )
	results = products.get_products();
	return results["total_results_count"], results["results"][:10];

def old_update_list(request, gid):
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

	
if __name__=="__main__":
	#find_prod("bournvita")
	pass

