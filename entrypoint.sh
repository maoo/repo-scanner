#!/bin/sh -l

if !ENV["GITHUB_TOKEN"]
  puts "Missing GITHUB_TOKEN"
  exit(1)
end

repo = ARGV[0]
config = ARGV[1]

if ENV["GITHUB_EVENT_PATH"]
  json = File.read(ENV.fetch("GITHUB_EVENT_PATH"))
  event = JSON.parse(json)
  repo = event["repository"]['full_name']
end

echo "Running Repo Scanner, repo '$repo' and config '$config'"
lein run -- --repo $repo --config $config
echo "::set-output name=exitcode::$?"