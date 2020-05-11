#!/bin/sh -l

if !ENV["GITHUB_TOKEN"]
  puts "Missing GITHUB_TOKEN"
  exit(1)
end

repo = ARGV[0]
config = ARGV[1]

echo "Running Repo Scanner, repo '$repo' and config '$config'"
lein run -- --repo $repo --config $config
echo "::set-output name=exitcode::$?"