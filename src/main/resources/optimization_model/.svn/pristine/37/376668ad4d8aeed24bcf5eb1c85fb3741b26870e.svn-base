import ast
import sets
from numpy import *

PARAM, VAR = range(2)

def read_single_value(file, cast_function = int):
	return cast_function(file.readline().split("=")[1])


def skip_lines(file, times):
	for i in range(times):
		file.readline()
		
def generate_service_table(attribute_list):
	table = "<<TABLE>\n"
	for (key,value) in attribute_list:
		table = table + "<TR> <TD>" + str(key) + "</TD> <TD>" +str(value) + "</TD> </TR>\n"
	return table + "</TABLE>>\n"

def generate_experiment_table(attribute_list):
	table = "label = <<TABLE>\n"
	table = table + "<TR><TD> Input </TD></TR>\n" 
	for (key, value, type) in filter( lambda (key, value, type): type == PARAM, attribute_list):
		table = table + "<TR> <TD>" + str(key) + "</TD> <TD>" + str(value) + "</TD> </TR>\n"
	
	table = table + "<TR><TD> Outputs </TD></TR>\n" 	
	for (key, value, type) in filter( lambda (key, value, type): type == VAR, attribute_list):
		table = table + "<TR> <TD>" + str(key) + "</TD> <TD>" + str(value) + "</TD> </TR>\n"	
		
	return table + "</TABLE>>\n"
		
		
def parse_list_of_tuples(file):
	list = []
	line = f.readline()
	while ("=" not in line) and line:
		list.extend(map(lambda (a): a.replace(";",""), line.split() ))
		line = f.readline()
	return map(lambda (tuple): ast.literal_eval(tuple), list)

def node_name(state, behavior, selflet):
	return "state_" + str(state) + "_" + str(behavior) + "_" + str(selflet)

def service_of_state_in_behavior(state, behavior):
	tuples = filter(lambda (bb, ii, ss): (bb == behavior and ii == state), service_of_state)
	return  str(filter(lambda (bb, ii, ss): (bb == behavior and ii == state), service_of_state)[0][2])

def remote_execution(behavior, state, selflet):
	return (len(filter(lambda (bb, ii, ss): (bb == behavior and ii == state and selflet == ss), state_remotely_executed)) > 0)
		
def make_node_list(selflet, behavior):
	states = filter(lambda (b, ts, ss, p): b == behavior, behavior_structure)
	node_string = ""
	unique_states = sets.Set(map(lambda (b, ts, ss, p): int(ts), states))
	unique_states = unique_states.union(sets.Set(map(lambda (b, ts, ss, p): int(ss), states)))
	
	for state in unique_states:
		where =  "remote" if remote_execution(behavior, state, selflet) else "local"
		search_lambda = lambda (b, n, i, d): b == behavior and i == state and n == selflet
		demand = str(filter(search_lambda, state_demand)[0][3])
		response_time = str(filter(search_lambda, response_time_of_state)[0][3])
		
		node_string += node_name(state, behavior, selflet) + " [ label = \"{ " + str(state)  + " | service: " + service_of_state_in_behavior(state, behavior) + " | " + "demand:" + demand +" | response: " + response_time + " | " + where + " }\" ]\n" 
		
	return node_string
	
def get_probability_of_edge(source, target, behavior):
	list = filter(lambda (bb, ts, ss, p): (bb == behavior and ss == source and ts == target), behavior_structure)
	return  str(list[0][3])
	
def make_edges(edges, selflet):
	edges_string = ""
	for (b, ts, ss, p) in edges:
		if ts == ss:
			continue
		edges_string +=  node_name(ss, b, selflet) + 	" -> " + node_name(ts, b, selflet) + " [label = \"" + get_probability_of_edge(ss, ts, b) + "\"]\n"
	return edges_string
	
def make_redirect_edges(selflet):
	edges_string = ""
	for (source_selflet, behavior, state, target_selflet, rate) in lambda_redirect:
		if target_selflet != selflet:
			continue
		destination_service = int(service_of_state_in_behavior(state, behavior))
		destination_behavior = get_behavior_implementing_service_in_selflet(destination_service, target_selflet)
		edges_string += node_name(state, behavior, source_selflet) + " -> " + node_name(1, destination_behavior, target_selflet) + " [label = \"" + str(rate) + "\"  style = \"dashed\"]\n"
	return edges_string

def get_remote_rate(behavior, selflet):
	tuple = filter( lambda(bb, nn, rate): (nn == selflet and bb == behavior), remote_rate)
	if len(tuple) == 0:
		return "-"
	return tuple[0][2]

def get_local_rate(behavior, selflet):
	tuple = filter( lambda(bb, nn, rate): (nn == selflet and bb == behavior), local_rate)
	if len(tuple) == 0:
		return "-"
	return tuple[0][2]

def get_behavior_implementing_service_in_selflet(service, selflet):
	behaviors_in_s = filter( lambda (b, n): n == selflet , behavior_in_selflet)
	behaviors_of_s = map( lambda (b, s): b   , filter( lambda(b, s): s == service, implementing_behaviors))
	
	for (b, s) in behaviors_in_s:
		if b in behaviors_of_s:
			return b
	
		# +"\", style = \"dashed\""
INPUT_FILE = "model_output"
f = open(INPUT_FILE,'r')

# read number of selflets
N = read_single_value(f)
# read number of services
S = read_single_value(f)
# read number of behaviors
B = read_single_value(f)
# read remote demand
DR = read_single_value(f, float)
# read max states 
MAX_STATES = read_single_value(f)

implementing_behaviors = f.readline().split()[3:]
# remove ";"
implementing_behaviors = map(lambda (a): a.replace(";",""), implementing_behaviors )
# create tuples
implementing_behaviors = map(lambda (tuple): ast.literal_eval(tuple), implementing_behaviors)

lambda_services_selflets = zeros([N, S], float)

skip_lines(f, 1)

for selflet in range(1, N+1):
	for service in range(1, S+1):
		service_rate = map(float, f.readline().split())
		index = map(lambda (n): int(n) - 1, service_rate[0:2])
		lambda_services_selflets[index[0], index[1]] = service_rate[2]

skip_lines(f, 2)


# read active selflets
active_selflets = []
for selflet in range(1, N+1):
	line = f.readline().split()
	if int(line[1]) == 1:
		active_selflets.append(selflet)

skip_lines(f, 2)

# read service in selflet
service_in_selflet = []
for selflet in range(1, N+1):
	for service in range(1, S+1):
		line = map(int, f.readline().split())
		if line[2] == 1:
			service_in_selflet.append((line[0], line[1]))

skip_lines(f, 2)

behavior_in_selflet = []
# read behavior of service in selflet
for behavior in range(1, B+1):
	for selflet in range(1, N+1):
		line = map(int, f.readline().split())
		if line[2] == 1:
			behavior_in_selflet.append((line[0], line[1]))
					
skip_lines(f, 2)
# read behavior structure
behavior_structure = parse_list_of_tuples(f)
#read service of state
service_of_state = parse_list_of_tuples(f)

skip_lines(f, 1)

state_locally_executed = []
state_remotely_executed = []
response_time_of_state = []


for behavior in range(1, B + 1):
	for state in range(1, MAX_STATES + 1):
		for selflet in range(1, N + 1):
			line = f.readline().split()
			if int(line[3]) == 1:
				state_locally_executed.append((behavior, state, selflet))
			if int(line[4]) == 1:
				state_remotely_executed.append((behavior, state, selflet))
				
			response_time_of_state.append( (behavior, selflet, state, float(line[5])) )
			
skip_lines(f,2)
lambda_redirect = []

for source_selflet in range(1, N+1):
	for behavior in range(1, B+1):
		for state in range(1, MAX_STATES+1):
			for target_selflet in range(1, N+1):
				line = f.readline().split()
				rate = float(line[4])
				if rate > 0:
					lambda_redirect.append((source_selflet, behavior, state, target_selflet, rate))


skip_lines(f,2)

remote_rate = []
for behavior in range(1, B+1):
	for selflet in range(1, N+1):
		line = f.readline().split()
		rate = float(line[2])
		if rate > 0:
			remote_rate.append((int(line[0]),int(line[1]), rate))

 
skip_lines(f,2)
local_rate = []
for behavior in range(1, B+1):
	for selflet in range(1, N+1):
		line = f.readline().split()
		rate = float(line[2])
		if rate > 0:
			local_rate.append((int(line[0]),int(line[1]), rate))



skip_lines(f,2)
response_time_at_selflet = []
for selflet in range(1, N+1):
	for service in range(1, S+1):
		line = f.readline().split()
		resp_time = float(line[2])
		if resp_time > 0:
			response_time_at_selflet.append( ( int(line[0]),int(line[1]),resp_time)  )


skip_lines(f,2)
state_demand = []
for behavior in range(1, B+1):
	for selflet in range(1, N+1):
		for state in range(1, MAX_STATES + 1):
			line = f.readline().split()
			state_demand.append( (behavior, selflet, state, float(line[3])) )
		

skip_lines(f,2)
selflet_util = []
for selflet in range(1, N+1):
	line = f.readline().split()
	selflet_util.append((int(line[0]), float(line[1])))

	
# close INPUT_FILE
f.close()

input_parameters = []
input_parameters.append(("Number of selflets", N, PARAM))
input_parameters.append(("Number of services", S, PARAM))
input_parameters.append(("Number of behaviors", B, PARAM))
input_parameters.append(("Remote demand", DR, PARAM))
input_parameters.extend(map( lambda(n, u): ("Util_"+str(n), u, VAR) ,selflet_util))


OUTPUT_FILE = "graph.dot"
f = open(OUTPUT_FILE,'w')
f.write("digraph G {\n")

f.write("\tnode [shape=record]; ")
f.write(generate_experiment_table(input_parameters))

#f.write("\trankdir=\"LR\";\n")
for selflet in active_selflets:
	f.write("\tsubgraph cluster_selflet" + str(selflet) + " {\n")
#	f.write("\trankdir=\"TB\";\n")
	for selflet1, service in filter(lambda (x, y): x==selflet, service_in_selflet):
		service_attributes = []
	#	behavior_tuple = filter(lambda (x, y, z): x == selflet and z == service, behavior_of_service_in_selflet)[0]
		behavior = get_behavior_implementing_service_in_selflet(service, selflet)

		service_attributes.append(("Service", service))
		service_attributes.append(("Behavior", behavior))
		
		resp_time = filter(lambda (x,y,z): x == selflet and y == service, response_time_at_selflet)
		if len(resp_time) > 0:
			service_attributes.append(("Response time", resp_time[0][2]))
			
		service_attributes.append(("Direct rate", lambda_services_selflets[selflet-1, service-1]))
		service_attributes.append(("Remote rate", get_remote_rate(behavior, selflet)))
		service_attributes.append(("Local rate", get_local_rate(behavior, selflet)))
		
		f.write("\t\tsubgraph cluster_service" + str(service) + "_" + str(selflet)+" {\n")
#		f.write("\t\trankdir=\"TB\";\n")
		
		f.write("\t\tlabel = " + generate_service_table(service_attributes) + "\n")
		f.write("\t\t" + make_node_list(selflet, behavior))
		f.write("\t\t")
		edges = filter(lambda (b, ts, ss, p): b == behavior, behavior_structure)
		f.write("\t\t" + make_edges(edges, selflet))
		f.write("\t\t}\n")
	f.write("\tlabel = \"selflet " + str(selflet) + "\";\n")
	f.write("\t}\n\n")
	
for selflet in active_selflets:
	f.write("\t\t" + make_redirect_edges(selflet))
	
f.write("}\n")
# close DOT FILE
f.close()


