import os
import subprocess

NUMBER_OF_SELFLETS = "n_selflets_"
LOAD = "load_"
SOLVING_TIME = "solving_time"

FIELDS = [NUMBER_OF_SELFLETS, LOAD, SOLVING_TIME]
# Paramters
MIN_SELFLETS = 1
MAX_SELFLETS = 100
MIN_LOAD = 1 
MAX_LOAD = 8 
LOG_FOLDER = "../logs"
fileList = os.listdir(LOG_FOLDER);


def log_file_contains(fileName, terms):
	for term in terms:
		#print "checking wheter " + term + " is in fileName"
		if not term in fileName:
			return False
	return True
	

def get_solving_time_from_log(fileName):
	file_path = LOG_FOLDER + "/" + fileName
	line = subprocess.check_output("cat " + file_path, shell=True)
	if "Problem appears to be infeasible" in line:
		return "INFEASIBLE"	
	#print line	
	line = subprocess.check_output("cat " + file_path + " | grep \"Total program time\"", shell=True)
	EXECUTION_TIME_COLUMN = 7
	return line.split()[EXECUTION_TIME_COLUMN]
	
def write_fields_in_file(file, fields):
	for field in fields:
		file.write(field + "\t")
	file.write("\n")

def write_tuple_in_file(file, tuple, fields):
	for field in fields:
		file.write(str(tuple[field]) + "\t")
	file.write("\n")

results_table = []
CSV_FILE = "../logs/results.csv"
results_file = open(CSV_FILE,"w")

write_fields_in_file(results_file, FIELDS)

for load in range(MIN_LOAD, MAX_LOAD):
	for n_selflets in range(MIN_SELFLETS, MAX_SELFLETS):
		load_fraction = float(load) / 10
		terms = [NUMBER_OF_SELFLETS + str(n_selflets), LOAD + str(load_fraction)]
		logFile = filter(lambda(fileName): log_file_contains(fileName, terms), fileList)
		
		if len(logFile) != 1:
			continue
		logFile = logFile[0] 
		result_row = {}
		result_row[NUMBER_OF_SELFLETS] = n_selflets
		result_row[LOAD] = load_fraction
		result_row[SOLVING_TIME] = get_solving_time_from_log(logFile)
		
		results_table.append(result_row)
		write_tuple_in_file(results_file, result_row, FIELDS)

results_file.close()

print "Results written in " + CSV_FILE
