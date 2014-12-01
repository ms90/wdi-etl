#utf-8
require "net/http"
require "net/https"
require "uri"
require 'rubygems'
require 'cgi'
require 'json'
require 'openssl'
ALIAS = ["ns:base.wikipedia_infobox.video_game.platforms"]#, "ns:cvg.computer_videogame.platforms", "ns:cvg.computer_videogame.developer", "ns:cvg.computer_videogame.publisher", "ns:cvg.computer_videogame.gameplay_modes", "ns:cvg.computer_videogame.cvg_genre"]

def get_content(mid)
  uri = URI.parse("https://www.googleapis.com/freebase/v1/rdf/" << mid)
  http = Net::HTTP.new(uri.host, uri.port)
  http.use_ssl = true
  http.verify_mode = OpenSSL::SSL::VERIFY_PEER
  request = Net::HTTP::Get.new(uri.request_uri)
  response = http.request(request)
  if response.code == "403"
    response.code  
  else
    response.body
  end
end

def get_hash(file)
  f = File.open("XML_out/#{file}", "r")
  open = 0
  close = 0
  res = {}
  ent = []
  t = 0
  f.each_line do |line|
    if line.include?("<videogame>")
      open += 1
      done = false
      id = ""
      title = ""
    end
    close += 1 if line.include?("</videogame>")
    ent << line 

    if (open == close) && (open > 0)
      t += 1
      open = 0
      close = 0
      ent.each do |row|
        if row.include?("<id>") and !done
          id = row.gsub("<id>", "").gsub("</id>", "").gsub("\t", "").gsub("\n", "")
          done = true
        end
        if row.include?("title")
          title = row.gsub("<title>", "").gsub("</title>", "").gsub("\t", "").gsub("\n", "")
        end
      end
      ent = []
      res.store(title, id)
      p t
    end
  end
  f.close
  res
end

def get_hashes(file)
  f = File.open("XML_out/#{file}", "r")
  tags = %w(id title release platform)
  res = []
  t = 0
  ent = {}
  f.each_line do |line|
    if !line.include?("</videogame>")
      tags.each do |tag|
        ent.store(tag, line.gsub("<#{tag}>", "").gsub("</#{tag}>", "").gsub("\t", "").gsub("\n", "")) if line.include?("<#{tag}>")
      end
    else
      ent = {}
      t += 1
      res << ent
      p t
    end

  end
  f.close
  res
end

task :test => :environment do
  p "test"
end

Encoding.default_external = Encoding::UTF_8
task :get_games => :environment do
  resource_ids = (1000..4188).to_a
  f = File.new("giantbomb_0.json", "a")
  resource_ids.each do |resource_id|
    link = "http://www.giantbomb.com/api/game/3030-"+resource_id.to_s+"/?api_key=76b2a205a21b85b9dc004dbe9cae59aadcf14c9b&format=json"
    uri = URI(link)
    s = Net::HTTP.get(uri)
    s.encode!('UTF-8', {:invalid => :replace, :undef => :replace, :replace => '?'})
    j = JSON.parse(s)
    if j["error"] == "OK"
      f.write(j["results"])
      p resource_id
    elsif j["error"] == "Object Not Found"
      p "404 " + resource_id.to_s
      next
    elsif j["error"].start_with?("Rate limit")
      p "Waiting"
      sleep(60*10)
      next
    else
      f.close
      p j["error"]
      return
    end
  end
  f.close
end

task :format => :environment do
  input = File.open("freebase.rdf", "r")
  res = File.new("freebase_1.rdf", "wb+")
  s = input.read
  res.write(s.gsub(".<", " .\n<"))
  input.close
  res.close
end

task :get_freebase => :environment do
  res = File.new("freebase.rdf", "wb+")
  gz_path = "c:/Users/Denis/AppData/Local/Temp/freebase-rdf-2014-10-05-00-00.gz"
  gz_file = File.open(gz_path, "rb") do |f|
    gz = Zlib::GzipReader.new(f)
    gz.each_line do |line|
      line.encode!('UTF-8', {:invalid => :replace, :undef => :replace, :replace => '?'})
      #if line.include?("cvg")# || line.include?("m.01mw1")
      if line.include?("cvg.computer_videogame")
        res.write(line)#(Base64.decode64(line))    
      end
    end
    gz.close
  end
  res.close
end

task :obtain_freebase => :environment do 
  OpenSSL::SSL::VERIFY_PEER = OpenSSL::SSL::VERIFY_NONE
  input = File.open("freebase.rdf", "r")
  output = File.new("freebase_out.rdf", "w+")

  content = input.read
  addr = []
  content.each_line do |line|
    str = line.split("\t")[0]
    url = str[1..-2] 
    addr << url
  end
  addr = addr.uniq
  addr.each do |address| 
    mid = address[27..-1].sub!(".", "/")  
    r = get_content(mid)
    p mid
    flag = 0
    if r == "403" && flag == 0
      p "sleeping"
      sleep(5*60)
      flag += 1
      redo
    elsif r == "403" && flag != 0
      next
    elsif r != "403"
     
    
      output.write(r)
    end
  end
  output.close
end

task :deeper => :environment do
  #OpenSSL::SSL::VERIFY_PEER = OpenSSL::SSL::VERIFY_NONE
  f = File.open("freebase_out.rdf", "r")
  f.each_line do |line|
    ALIAS.each do |allias|
      if line.strip.start_with?(allias)
        p line
        index = line.index("m.")
        mid_2 = line[index..-3].sub(".", "/")
        rd = get_content(mid_2)
        if rd == "403"
          p "sleeping"
          sleep(5*60)
          redo
        else
          m = File.new("mids/"<<mid_2.sub("/", "."), "w+")
          m.write(rd)
          m.close
        end
      end
    end
  end
  f.close
end

task :is_ok_json => :environment do
  f = File.open("C:/Users/Denis/git/wdi-id-resolution/resources/VideoGames/XML/giantbomb.xml", "r")
  #g = File.open("short_1.json", "w")
  open = 0
  close = 0
  ent = 0
  ["genre"].each do |tag|
    ln = 0
    f.each_line do |line|
      ln +=1
      if line.include?("<#{tag}>")
        open += 1
        p line
      end
      if line.include?("</#{tag}>")
        close += 1 
        p line
      end
      if open == close
        ent +=1
        open = 0
        close = 0
      end
      if open - close > 1
        p ln
        p tag
        return
      end
    end
    #out.write(s)
  end
  p "enity " << ent.to_s
  f.close
end

task :add_comas => :environment do
  f = File.open("input", "r")
  out = File.open("output", "w") 
  f.each_line do |line|
    s = line.gsub("}{", "}, {")
    p s
    out.write(s)
  end
  out.close
end

task :format_json => :environment do
  f = File.open("input", "r")
  res = File.new("last_formated.json", "w")
  f.each_line do |line|
    line.gsub!("=>", ":")
    line.gsub!("nil", "[]")
    line.gsub!("}{", "}, {")
    res.write(line)
    p line
  end
  f.close
  res.close
end

task :merge => :environment do
  main = File.open("freebase_out.rdf", "r")
  dir = Dir.entries("mids")
  res = File.open("free_res.rdf", "w")

  main.each_line do |line|
    tmp = line
    dir.each do |mid|
      unless mid == "." || mid == ".."
        p mid
        mid_file = File.open("hope_1/"<<mid, "r")
        mid_content = mid_file.read
        tmp = line.gsub(mid, mid_content)
        mid_file.close
      end
    end
    res.write(tmp)
    
  end
  main.close
  res.close
end

task :clean_mids => :environment do
  dir = Dir.entries("mids")
  dir.each do |mid|
    if mid == "." || mid == ".."
      next
    else
      input = File.open("mids/"<<mid, "r")
      output = File.open("hope_1/"<<mid, "w")
      input.each_line do |line|
        unless dir.any?{|mid| line.include?(mid)}
          output.write(line)
          p "+"
        end
        # unless line.include?("@en")
        #   f.seek(-line.length, IO::SEEK_CUR)
        #   f.write(" " * (line.length - 1))
        #   f.write("\n")
        #   p "not english deleted"
        # end
      end
      input.close
      output.close
    end
  end
end

task :short_freebase => :environment do
  input = File.open("freebase_out.rdf", "r")
  #output = File.open("frebase_short.rdf", "w")
  t = 0
  input.each_line do |line|
    #output.write(line)
    if line.include?('@prefix xsd: <http://www.w3.org/2001/XMLSchema#>.')
      t += 1
      p t
    end
     #return if t == 750
  end
  p t
  input.close
  #output.close
end

task :to_json => :environment do
  input = File.open("freebase_out - Copy - Copy.rdf", "r")
  output = File.open("free.json", "a")
  output.write("[")
  input.each_line do |line|
    if !line.start_with("\t")
      output.write("{"+ "\"#{line}\":")
    else line.start_with("\t")
      str = line.strip("\t")
      str.each do
      end
    end
  end
end

task :get_gid => :environment do
  rdf = File.open("freebase_out.rdf", "r")
  in_json = File.open("freebase.json", "r")
  json = JSON.parse(in_json.read)
  entities = []
  entity = {}
  rdf.each_line do |line|
    if line.start_with?(" ")
      if line.include?("object.name")
        tmp = line.split[1..-1].join.split("@")[0].gsub("\"", "")
        entity.store("typeobjectname", tmp)
      elsif line.include?("giantbomb.game")
        arr = line.split(" ")
        a = arr[1].split(".")
        entity.store("gid", a[3])
      end        
    else
      entities << entity if entity.keys.count == 2
      entity = {}
    end
  end

  json.each do |fen|
    entities.each do |en|
      fen.merge!(en) if fen["typeobjectname"] == en["typeobjectname"]
    end
  end

  n = File.open("with_gid.json", "w")
  n.write(json)
  n.close
  n = File.open("with_gid.json", "r")
  content = n.read
  n.close

  content.gsub!("=>", ": ")
  n = File.open("with_gid.json", "w")
  n.write(content)
  n.close
  #search and push to free.json 
  in_json.close
  json.each {|j| p j }
  rdf.close
  #entities.each {|e| p e}
  #p entities.count
end

task :get_gold => :environment do
  path = 'C:/Users/Denis/git/wdi-id-resolution/resources/VideoGames/XML/'
  giantbomb = 'giantbomb.xml'
  dbpedia = 'dbpedia.xml'
  giantbomb = File.open(path<<giantbomb, 'r')
  dbpedia = File.open(path<<dbpedia, 'r')
  giantbomb = giantbomb.read
  dbpedia = dbpedia.read
end 
task :normalize => :environment do
  files = ["giantbomb.xml", "dbpedia.xml", "thegamesdb.xml", "freebase.xml"]
  files.each do |file|
    input = File.open("XML/#{file}", "r")
    output = File.open("XML_out/#{file}", "w")
    del = [" a ", " the ", ",", ".", ":", "-"]
    input.each_line do |line|
      line.downcase!
      del.each { |sym| line.gsub!(sym, "") } if line.include?("<title>")
      p file
      output.write(line)
    end
    input.close
    output.close
  end
end

task :to_files => :environment do
  db, gb, tgdb, fb = []

  files = ["freebase.xml"]
  files.each do |file|
    ids = []
    titles = []
    input = File.open("XML_out/#{file}", "r")
    input.each_line do |line|
      titles << line if line.include?("<title>")
      ids << line if line.include?("<id>")
      p "+"
    end
    input.close
    output_t = File.new("XML_out/#{file}_titles.cvs", "w")
    output_t.write(titles.join("|"))
    output_i = File.new("XML_out/#{file}_ids.csv", "w")
    output_i.write(ids.join("|"))
    output_i.close
    output_t.close
  end
end

task :diff => :environment do
  files = ["XML_out/giantbomb.xml_titles.cvs", "XML_out/dbpedia.xml_titles.cvs", "XML_out/thegamesdb.xml_titles.cvs", "XML_out/freebase.xml_titles.cvs"]
  # gntbmb = File.open(files[0], "r").read.split("|")
  # dvpbd = File.open(files[1], "r").read.split("|")
  # tgmsdb =  File.open(files[2], "r").read.split("|")
  # frbs =  File.open(files[3], "r").read.split("|")  4492_total

  in_a = File.open("XML_out/diffs/4492_total.csv", "r")
  a = in_a.read.split("|")
  in_a.close

  in_b = File.open(files[3], "r")
  b = in_b.read.split("|")
  in_b.close

  in_c = File.open(files[2], "r")
  c = in_c.read.split("|")
  in_c.close

  a_b = (a - b).uniq
 # p a_b.size

  b_a = (b - a).uniq
 # p b_a.size

  ab = (a + b).uniq
 # p ab.size

  ba = (a_b + b_a).uniq
 # p ba.size

  d = (ab - ba).uniq
  p "-------"
  p d.size
  #p "res!"
  #res.uniq!
  #p res.size

  # d_c = (d - c).uniq
  # c_d = (c - d).uniq
  # dc = (d + c).uniq
  # cd = (d_c + c_d).uniq
  # r = (dc - cd).uniq

  # p "Aleeeee Op!"
  # p r.size



  # out = File.new("XML_out/diffs/#{r.size}_total.csv", "w")
  # out.write(r.join("|"))
  # out.close
end

task :diff_3 => :environment do
  # a = File.open("XML_out/diffs/7431_giantbomb_thegamesdb.csv")
  # b = File.open("XML_out/diffs/7077_dbpedia_thegamesdb.csv")
  # c = File.open("XML_out/diffs/#{res.size}_dbpedia_thegamesdb.csv")
  inputs = ["thegamesdb.xml", "giantbomb.xml", "dbpedia.xml"]
  matches = File.open("XML_out/diffs/4492_total.csv", "r").read.split("|")
  inputs.each do |f|
    #sleep 60
    input = File.open("XML_out/#{f}", "r")
    output = File.open("XML_out/_out_#{f}", "w")
    open = 0
    close = 0
    ent = []
    output.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
    output.write("<data xsi:nonamespaceschemalocation=\"d:/uni/webdat~1/project/target.xsd\" xmlns:xsi=\"http://www.w3.org/2001/xmlschema-instance\">")
    input.each_line do |line|
      open += 1 if line.include?("<videogame>")
      close += 1 if line.include?("</videogame>")
      ent << line 
      if (open == close) && (open > 0)
       # ent <<
        open = 0
        close = 0
        ent.each do |row|
          if row.include?("<title>")
            if matches.include?(row)
              output.write(ent.join)
              p row
            end
          end
        end
        ent = []
      end
    end
    output.write("</data>")
    output.close
    input.close
  end
end

task :mine_gold => :environment do
  inputs = ["merged_thegamesdb.xml", "_out_dbpedia.xml"]
  hashes = []
  inputs.each do |input|
    p input
    hashes << get_hash(input)
  end
  f1 = File.open("XML_out/csv/dbpedia_merged_gamesdb_csv.csv", "w")
  hashes[1].each_key do |key| 
    #if hashes[0] and hashes[2]

      if hashes[0][key] and hashes[1][key]
        l = "#{hashes[1][key]},#{hashes[0][key]}\n"
        p l 
        f1.write(l)
      end
    #end
  end 
  f1.close 

  # f2 = File.open("XML_out/csv/dbpedia_merged_gamesdb_csv.csv", "w")
  # hashes[2].each_key do |key| 
  #   if  hashes[0][key] and hashes[2][key]
  #     l = "#{hashes[2][key]},#{hashes[0][key]}\n"
  #     p l 
  #     f2.write(l)
  #   end
  # end  
  # f2.close

end

task :merge_gamesdb => :environment do
  output = File.open("XML_out/merged_thegamesdb.xml", "w")
  output.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
  output.write("<data xsi:nonamespaceschemalocation=\"d:/uni/webdat~1/project/target.xsd\" xmlns:xsi=\"http://www.w3.org/2001/xmlschema-instance\">")
  counter = 0
  hashes1 = get_hashes("thegamesdb.xml")
  res = []

  hashes1.each do |element|
    ent = {}

    platform = [element["platform"]]

    ent.store("id", element["id"])
    ent.store("title", element["title"])
    ent.store("release", element["release"])
    ent.store("platform", platform)
    
    hashes1.each do |element2|
      if element["title"] == element2["title"] and hashes1.index(element) != hashes1.index(element2)
        ent.store("platform", platform << element2["platform"])
      end
    end
    
    if !res.include?(ent["title"])
      res << ent["title"]
      output.write("\t<videogame>\n")
      output.write("\t\t<id>#{ent["id"]}</id>\n")
      output.write("\t\t<title>#{ent["title"]}</title>\n")
      output.write("\t\t<release>#{ent["release"]}</release>\n")
      ent["platform"].each { |p| output.write("\t\t<platform>#{p}</platform>\n") }
      output.write("\t</videogame>\n")
      counter += 1
      p "recorded: "<<counter.to_s
    end
  end
  output.write("</data>")
  output.close
end

task :change_id => :environment do
  files = ["merged_thegamesdb.xml", "dbpedia.xml", "giantbomb.xml"]
  ids = %w(gdb dbp gbmb)
  files.each_with_index do |file, index|
    input  = File.open("XML_out/old_ids/" << file, "r")
    output = File.open("XML_out/new_ids/" << file, "w")
    input.each_line do |line|
      s = line
      s.gsub!("<id>", "<id>#{ids[index]}")
      output.write(s)
      p file
    end
  end
end

task :change_id_m => :environment do
  files = ["gold_1.csv", "gold_2.csv"]
  ids = %w(gbmb gdb dbp)
  files.each_with_index do |file, index|
    input  = File.open("XML_out/old_ids/" << file, "r")
    output = File.open("XML_out/new_ids/" << file, "w")
    input.each_line do |line|
      s = line
      s.insert(0, "dbp").gsub!(",", ",#{ids[index]}")
      p s
      output.write(s)
    end
    output.close
  end
end

task :get_sizes => :environment do 
   files = ["thegamesdb.xml", "dbpedia.xml", "giantbomb.xml"]  
   c = []
   files.each do |file|
    f = File.open("XML_out/new_ids/#{file}", "r")
    count = 0
    f.each_line do |line|
      count += 1 if line.include?("<videogame>")
      p count
    end
    c << count
   end
    p c
end