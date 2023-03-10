import os

dir_path = 'logs/'
node_dict = {}

for file_name in os.listdir(dir_path):
    if file_name.endswith('.log'):
        file_path = os.path.join(dir_path, file_name)
        with open(file_path) as f:
            lines = f.readlines()
            if len(lines) > 2 and lines[2].startswith('peer identity:'):
                peer_hash = lines[2][15:].strip()
                node_dict[peer_hash] = file_name[:-4]

# Print the node dictionary formated
for key, value in node_dict.items():
    print(value + ': ' + key)

# Choose a node to see the flows ask user input
node = 'node_'+ input('Choose a node (152, 160, 186, 187): ')
request_blocks = []
first = True

print()
# Go through every line of the node's log file and match want-have
with open(os.path.join(dir_path, node + '.log')) as f:
    i = 0
    for line in f:
        i += 1
        # messages sent by the node
        if 'WANT_HAVE' in line or 'WANT_BLOCK' in line:
            #print(i)        # line debug
            line_parts = line.split()
            
            peer = node_dict.get(line_parts[12][1:-2])
            message = line_parts[6][1:-2]
            cid = '...' + line_parts[8][1:-2][-3:]
            timestamp = line_parts[0][11:-5]

            if (message == 'WANT_HAVE' and first):
                first = False
                request_blocks.append(cid)

            if (message == 'WANT_BLOCK'):
                request_blocks.append(cid)

            print(timestamp + ': ' + node + ' sent ' + message + ' (' + cid + ') to ' + peer)

        if 'bitswap	[recv] block not in wantlist' in line:
            #print(i)        # line debug
            line_parts = line.split()
            
            cid = '...' +  line_parts[5][4:-1][-3:]
            peer = node_dict.get(line_parts[9][5:])
            timestamp = line_parts[0][11:-5]

            print(timestamp + ': ' + node + ' received block (' + cid + ') from ' + peer + ' that is not in the wantlist')

        # messages received by the node
        if 'bitswap	[recv] block;' in line:
            #print(i)        # line debug
            line_parts = line.split()
            
            cid = '...' +  line_parts[5][4:-1][-3:]
            peer = node_dict.get(line_parts[6][5:])
            timestamp = line_parts[0][11:-5]

            print(timestamp + ': ' + node + ' received block (' + cid + ') from ' + peer)


print(f'Number of blocks requested: {len(request_blocks)}')
#print(f'Blocks requested: {request_blocks}')
